package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.buscarpedido;

import br.com.pedro.lemos.apipedidos.application.exception.PedidoNaoEncontradoException;
import br.com.pedro.lemos.apipedidos.application.usecase.buscarpedidousecase.BuscarPedidoUseCase;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BuscarPedidoController.class)
@ContextConfiguration(classes = {BuscarPedidoController.class, BuscarPedidoControllerTest.TestConfig.class})
class BuscarPedidoControllerTest {

    @Configuration
    static class TestConfig {
        @Bean
        @Primary
        public BuscarPedidoUseCase buscarPedidoUseCase() {
            return Mockito.mock(BuscarPedidoUseCase.class);
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BuscarPedidoUseCase buscarPedidoUseCase;

    @BeforeEach
    void setUp() {
        reset(buscarPedidoUseCase);
    }

    @Test
    void deveBuscarPedidoComSucesso() throws Exception {
        
        Long pedidoId = 123L;
        Pedido pedido = new Pedido();
        when(buscarPedidoUseCase.buscar(pedidoId)).thenReturn(pedido);

        mockMvc.perform(get("/pedidos/{pedidoId}", pedidoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pedido").exists());

        verify(buscarPedidoUseCase, times(1)).buscar(pedidoId);
    }

    @Test
    void deveRetornar404QuandoPedidoNaoEncontrado() throws Exception {
        
        Long pedidoId = 999L;
        when(buscarPedidoUseCase.buscar(pedidoId))
                .thenThrow(new PedidoNaoEncontradoException(pedidoId));

        mockMvc.perform(get("/pedidos/{pedidoId}", pedidoId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.codigoErro").value("PNE"))
                .andExpect(jsonPath("$.data.motivoErro").exists());

        verify(buscarPedidoUseCase, times(1)).buscar(pedidoId);
    }

    @Test
    void deveRetornar500QuandoOcorreErroInesperado() throws Exception {
        
        Long pedidoId = 123L;
        when(buscarPedidoUseCase.buscar(pedidoId))
                .thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(get("/pedidos/{pedidoId}", pedidoId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data.codigoErro").value("SI"))
                .andExpect(jsonPath("$.data.motivoErro").value("Serviço indisponível"));

        verify(buscarPedidoUseCase, times(1)).buscar(pedidoId);
    }

    @Test
    void deveAceitarDiferentesValoresDeId() throws Exception {
        
        Long pedidoId = 456789L;
        Pedido pedido = new Pedido();
        when(buscarPedidoUseCase.buscar(pedidoId)).thenReturn(pedido);

        mockMvc.perform(get("/pedidos/{pedidoId}", pedidoId))
                .andExpect(status().isOk());

        verify(buscarPedidoUseCase, times(1)).buscar(pedidoId);
    }
}