package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.listarpedidos;

import br.com.pedro.lemos.apipedidos.application.usecase.listarpedidosusecase.ListarPedidosUseCase;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ListarPedidosController.class)
@ContextConfiguration(classes = {ListarPedidosController.class, ListarPedidosControllerTest.TestConfig.class})
class ListarPedidosControllerTest {

    @Configuration
    static class TestConfig {
        @Bean
        @Primary
        public ListarPedidosUseCase listarPedidosUseCase() {
            return Mockito.mock(ListarPedidosUseCase.class);
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ListarPedidosUseCase listarPedidosUseCase;

    @BeforeEach
    void setUp() {
        reset(listarPedidosUseCase);
    }

    private final String correlationId = UUID.randomUUID().toString();

    @Test
    void deveListarPedidosComSucesso() throws Exception {
        // Cria pedidos com todos os campos necessários inicializados
        Pedido pedido1 = new Pedido();
        pedido1.setIdPedido(1L);
        pedido1.setCodigoIdentificacaoCliente("cliente1");
        pedido1.setStatusPedido("ATIVO");
        pedido1.setDescricaoProdutos(new ArrayList<>());
        pedido1.setDataHoraCriacaoPedido("2025-01-01T00:00:00");
        pedido1.setTransactionId("txn1");

        Pedido pedido2 = new Pedido();
        pedido2.setIdPedido(2L);
        pedido2.setCodigoIdentificacaoCliente("cliente2");
        pedido2.setStatusPedido("ATIVO");
        pedido2.setDescricaoProdutos(new ArrayList<>());
        pedido2.setDataHoraCriacaoPedido("2025-01-01T00:00:00");
        pedido2.setTransactionId("txn2");

        // Cria uma lista completamente mutável
        List<Pedido> pedidos = new ArrayList<>();
        pedidos.add(pedido1);
        pedidos.add(pedido2);

        // Usa PageRequest específico para garantir compatibilidade
        Page<Pedido> pageDePedidos = new PageImpl<>(pedidos, org.springframework.data.domain.PageRequest.of(0, 10), pedidos.size());
        when(listarPedidosUseCase.listar(any(Pageable.class))).thenReturn(pageDePedidos);

        mockMvc.perform(get("/pedidos")
                        .header("correlationId", correlationId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));

        verify(listarPedidosUseCase, times(1)).listar(any(Pageable.class));
    }

    @Test
    void deveRetornar204QuandoNaoHaPedidosAtivos() throws Exception {
        when(listarPedidosUseCase.listar(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/pedidos")
                        .header("correlationId", correlationId))
                .andExpect(status().isNoContent());

        verify(listarPedidosUseCase, times(1)).listar(any(Pageable.class));
    }

    @Test
    void deveRetornar500ParaOutrasExcecoes() throws Exception {
        when(listarPedidosUseCase.listar(any(Pageable.class))).thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(get("/pedidos")
                        .header("correlationId", correlationId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data.codigoErro").value("SI"))
                .andExpect(jsonPath("$.data.motivoErro").value("Serviço indisponível"));

        verify(listarPedidosUseCase, times(1)).listar(any(Pageable.class));
    }
}