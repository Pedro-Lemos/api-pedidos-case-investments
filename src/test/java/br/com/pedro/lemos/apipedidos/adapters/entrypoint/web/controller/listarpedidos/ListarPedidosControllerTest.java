package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.listarpedidos;

import br.com.pedro.lemos.apipedidos.application.exception.PedidosInativosException;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Test
    void deveListarPedidosComSucesso() throws Exception {
        List<Pedido> pedidos = List.of(new Pedido(), new Pedido());
        when(listarPedidosUseCase.listar()).thenReturn(pedidos);

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));

        verify(listarPedidosUseCase, times(1)).listar();
    }

    @Test
    void deveRetornar204QuandoNaoHaPedidosAtivos() throws Exception {
        when(listarPedidosUseCase.listar()).thenThrow(new PedidosInativosException());

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.data.codigoErro").value("PIE"))
                .andExpect(jsonPath("$.data.motivoErro").exists());

        verify(listarPedidosUseCase, times(1)).listar();
    }

    @Test
    void deveRetornar500QuandoOcorreErroInesperado() throws Exception {
        when(listarPedidosUseCase.listar()).thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data.codigoErro").value("SI"))
                .andExpect(jsonPath("$.data.motivoErro").value("Serviço indisponível"));

        verify(listarPedidosUseCase, times(1)).listar();
    }
}