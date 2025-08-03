package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.efetuarpedido;

import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.efetuarpedido.request.EfetuarPedidoRequestV1;
import br.com.pedro.lemos.apipedidos.application.exception.PedidoEmAndamentoException;
import br.com.pedro.lemos.apipedidos.application.exception.ProdutoNaoDisponivelException;
import br.com.pedro.lemos.apipedidos.application.exception.ProdutoNaoEncontradoException;
import br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase.EfetuarPedidoUseCase;
import br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase.model.SolicitacaoEfetuarPedido;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EfetuarPedidoController.class)
@ContextConfiguration(classes = {EfetuarPedidoController.class, EfetuarPedidoControllerTest.TestConfig.class})
class EfetuarPedidoControllerTest {

    @Configuration
    static class TestConfig {
        @Bean
        @Primary
        public EfetuarPedidoUseCase efetuarPedidoUseCase() {
            return Mockito.mock(EfetuarPedidoUseCase.class);
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EfetuarPedidoUseCase efetuarPedidoUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Reset completo do mock antes de cada teste
        reset(efetuarPedidoUseCase);
    }

    @Test
    void deveEfetuarPedidoComSucesso() throws Exception {
        
        String transactionId = "TXN-12345";
        EfetuarPedidoRequestV1 request = Mock.criarRequestValido();

        mockMvc.perform(post("/pedidos")
                        .header("transactionId", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.mensagem").value("Pedido efetuado com sucesso!"))
                .andExpect(jsonPath("$.data.idPedido").exists());

        verify(efetuarPedidoUseCase, times(1)).efetuar(any(SolicitacaoEfetuarPedido.class));
    }

    @Test
    void deveRetornar422QuandoPedidoEmAndamento() throws Exception {
        
        String transactionId = "TXN-12345";
        EfetuarPedidoRequestV1 request = Mock.criarRequestValido();

        doThrow(new PedidoEmAndamentoException(123L))
                .when(efetuarPedidoUseCase)
                .efetuar(any(SolicitacaoEfetuarPedido.class));

        mockMvc.perform(post("/pedidos")
                        .header("transactionId", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data.codigoErro").value("PEM"))
                .andExpect(jsonPath("$.data.motivoErro").value("Existe um pedido em andamento para o idPedido: 123"));

        verify(efetuarPedidoUseCase, times(1)).efetuar(any(SolicitacaoEfetuarPedido.class));
    }

    @Test
    void deveRetornar422QuandoProdutoNaoDisponivel() throws Exception {
        
        String transactionId = "TXN-12345";
        EfetuarPedidoRequestV1 request = Mock.criarRequestValido();

        doThrow(new ProdutoNaoDisponivelException("Notebook Dell"))
                .when(efetuarPedidoUseCase)
                .efetuar(any(SolicitacaoEfetuarPedido.class));

        mockMvc.perform(post("/pedidos")
                        .header("transactionId", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data.codigoErro").value("PND"));

        verify(efetuarPedidoUseCase, times(1)).efetuar(any(SolicitacaoEfetuarPedido.class));
    }

    @Test
    void deveRetornar404QuandoProdutoNaoEncontrado() throws Exception {
        
        String transactionId = "TXN-12345";
        EfetuarPedidoRequestV1 request = Mock.criarRequestValido();

        doThrow(new ProdutoNaoEncontradoException(999L))
                .when(efetuarPedidoUseCase)
                .efetuar(any(SolicitacaoEfetuarPedido.class));

        mockMvc.perform(post("/pedidos")
                        .header("transactionId", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.codigoErro").value("PNE"))
                .andExpect(jsonPath("$.data.motivoErro").value("Não foi encontrado o produto: 999"));

        verify(efetuarPedidoUseCase, times(1)).efetuar(any(SolicitacaoEfetuarPedido.class));
    }

    @Test
    void deveRetornar500QuandoErroGenerico() throws Exception {
        
        String transactionId = "TXN-12345";
        EfetuarPedidoRequestV1 request = Mock.criarRequestValido();

        doThrow(new RuntimeException("Erro inesperado"))
                .when(efetuarPedidoUseCase)
                .efetuar(any(SolicitacaoEfetuarPedido.class));

        mockMvc.perform(post("/pedidos")
                        .header("transactionId", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data.codigoErro").value("SI"))
                .andExpect(jsonPath("$.data.motivoErro").value("Serviço indisponível"));

        verify(efetuarPedidoUseCase, times(1)).efetuar(any(SolicitacaoEfetuarPedido.class));
    }

    @Test
    void deveRetornar400QuandoTransactionIdNaoInformado() throws Exception {
        
        EfetuarPedidoRequestV1 request = Mock.criarRequestValido();

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(efetuarPedidoUseCase, never()).efetuar(any(SolicitacaoEfetuarPedido.class));
    }

//    @Test
//    void deveRetornar400QuandoBodyInvalido() throws Exception {
//        
//        String transactionId = "TXN-12345";
//        String requestInvalido = "{\"invalid\": \"json\"}";
//
//         & Then
//        mockMvc.perform(post("/pedidos")
//                        .header("transactionId", transactionId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestInvalido))
//                .andExpect(status().isBadRequest());
//
//        verify(efetuarPedidoUseCase, never()).efetuar(any(SolicitacaoEfetuarPedido.class));
//    }

    @Test
    void deveProcessarRequestComMultiplosProdutos() throws Exception {
        
        String transactionId = "TXN-12345";
        EfetuarPedidoRequestV1 request = Mock.criarRequestComMultiplosProdutos();

        mockMvc.perform(post("/pedidos")
                        .header("transactionId", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.mensagem").value("Pedido efetuado com sucesso!"));

        verify(efetuarPedidoUseCase, times(1)).efetuar(any(SolicitacaoEfetuarPedido.class));
    }

    @Test
    void deveValidarConteudoDoRequestPassadoParaUseCase() throws Exception {
        
        String transactionId = "TXN-12345";
        EfetuarPedidoRequestV1 request = Mock.criarRequestValido();

        
        mockMvc.perform(post("/pedidos")
                        .header("transactionId", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(efetuarPedidoUseCase, times(1)).efetuar(argThat(solicitacao ->
                solicitacao.getCodigoIdentificacaoCliente().equals("CLI-001") &&
                        solicitacao.getProduto().size() == 1 &&
                        solicitacao.getTransactionId().equals(transactionId)
        ));
    }
}