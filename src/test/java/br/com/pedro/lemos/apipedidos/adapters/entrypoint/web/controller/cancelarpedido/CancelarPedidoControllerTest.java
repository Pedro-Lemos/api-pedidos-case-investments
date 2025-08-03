package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.cancelarpedido;

import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.cancelarpedido.request.CancelarPedidoRequestV1;
import br.com.pedro.lemos.apipedidos.application.exception.PedidoCanceladoException;
import br.com.pedro.lemos.apipedidos.application.exception.PedidoNaoEncontradoException;
import br.com.pedro.lemos.apipedidos.application.usecase.cancelarpedidousecase.CancelarPedidoUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CancelarPedidoController.class)
@ContextConfiguration(classes = {CancelarPedidoController.class, CancelarPedidoControllerTest.TestConfig.class})
class CancelarPedidoControllerTest {

    @Configuration
    static class TestConfig {
        @Bean
        @Primary
        public CancelarPedidoUseCase cancelarPedidoUseCase() {
            return Mockito.mock(CancelarPedidoUseCase.class);
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CancelarPedidoUseCase cancelarPedidoUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        reset(cancelarPedidoUseCase);
    }

    @Test
    void deveCancelarPedidoComSucesso() throws Exception {
        Long pedidoId = 123L;
        String motivoCancelamento = "Cliente desistiu da compra";
        CancelarPedidoRequestV1 request = new CancelarPedidoRequestV1(motivoCancelamento);

        doNothing().when(cancelarPedidoUseCase).cancelar(pedidoId, motivoCancelamento);

        mockMvc.perform(post("/pedidos/{pedidoId}/cancelamentos", pedidoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.mensagem").value("Pedido cancelado com sucesso"));

        verify(cancelarPedidoUseCase, times(1)).cancelar(pedidoId, motivoCancelamento);
    }

    @Test
    void deveRetornar404QuandoPedidoNaoEncontrado() throws Exception {
        Long pedidoId = 999L;
        String motivoCancelamento = "Cliente desistiu da compra";
        CancelarPedidoRequestV1 request = new CancelarPedidoRequestV1(motivoCancelamento);

        doThrow(new PedidoNaoEncontradoException(pedidoId))
                .when(cancelarPedidoUseCase).cancelar(pedidoId, motivoCancelamento);

        mockMvc.perform(post("/pedidos/{pedidoId}/cancelamentos", pedidoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.codigoErro").value("PNE"))
                .andExpect(jsonPath("$.data.motivoErro").exists());

        verify(cancelarPedidoUseCase, times(1)).cancelar(pedidoId, motivoCancelamento);
    }

    @Test
    void deveRetornar422QuandoPedidoJaCancelado() throws Exception {
        Long pedidoId = 123L;
        String motivoCancelamento = "Cliente desistiu da compra";
        CancelarPedidoRequestV1 request = new CancelarPedidoRequestV1(motivoCancelamento);

        doThrow(new PedidoCanceladoException(pedidoId))
                .when(cancelarPedidoUseCase).cancelar(pedidoId, motivoCancelamento);

        mockMvc.perform(post("/pedidos/{pedidoId}/cancelamentos", pedidoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data.codigoErro").value("PEM"))
                .andExpect(jsonPath("$.data.motivoErro").exists());

        verify(cancelarPedidoUseCase, times(1)).cancelar(pedidoId, motivoCancelamento);
    }

    @Test
    void deveRetornar500QuandoOcorreErroInesperado() throws Exception {
        Long pedidoId = 123L;
        String motivoCancelamento = "Cliente desistiu da compra";
        CancelarPedidoRequestV1 request = new CancelarPedidoRequestV1(motivoCancelamento);

        doThrow(new RuntimeException("Erro inesperado"))
                .when(cancelarPedidoUseCase).cancelar(pedidoId, motivoCancelamento);

        mockMvc.perform(post("/pedidos/{pedidoId}/cancelamentos", pedidoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data.codigoErro").value("SI"))
                .andExpect(jsonPath("$.data.motivoErro").value("Serviço indisponível"));

        verify(cancelarPedidoUseCase, times(1)).cancelar(pedidoId, motivoCancelamento);
    }

    @Test
    void deveRetornar400QuandoRequestInvalido() throws Exception {
        Long pedidoId = 123L;
        String requestInvalido = "{}";

        mockMvc.perform(post("/pedidos/{pedidoId}/cancelamentos", pedidoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestInvalido))
                .andExpect(status().isBadRequest());

        verify(cancelarPedidoUseCase, never()).cancelar(anyLong(), anyString());
    }

    @Test
    void deveAceitarDiferentesValoresDeId() throws Exception {
        Long pedidoId = 456789L;
        String motivoCancelamento = "Produto indisponível";
        CancelarPedidoRequestV1 request = new CancelarPedidoRequestV1(motivoCancelamento);

        doNothing().when(cancelarPedidoUseCase).cancelar(pedidoId, motivoCancelamento);

        mockMvc.perform(post("/pedidos/{pedidoId}/cancelamentos", pedidoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.mensagem").value("Pedido cancelado com sucesso"));

        verify(cancelarPedidoUseCase, times(1)).cancelar(pedidoId, motivoCancelamento);
    }
}