package br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase;

import br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository.PedidoRepository;
import br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository.ProdutoRepository;
import br.com.pedro.lemos.apipedidos.application.exception.PedidoEmAndamentoException;
import br.com.pedro.lemos.apipedidos.application.exception.ProdutoNaoDisponivelException;
import br.com.pedro.lemos.apipedidos.application.exception.ProdutoNaoEncontradoException;
import br.com.pedro.lemos.apipedidos.application.service.GeradorIdPedidoService;
import br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase.model.SolicitacaoEfetuarPedido;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import br.com.pedro.lemos.apipedidos.domain.entity.Produto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EfetuarPedidoUseCaseImplTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private GeradorIdPedidoService geradorIdPedidoService;

    @InjectMocks
    private EfetuarPedidoUseCaseImpl efetuarPedidoUseCase;

    @Test
    void deveEfetuarPedidoComSucessoQuandoTodosOsProdutosEstaoDisponiveis() {
        // Given
        SolicitacaoEfetuarPedido solicitacao = criarSolicitacaoEfetuarPedido();
        Produto produtoDisponivel = criarProdutoEmEstoque(1L, 10);
        Long idPedidoGerado = 12345L;

        when(produtoRepository.findById(1L)).thenReturn(produtoDisponivel);
        when(geradorIdPedidoService.gerarId()).thenReturn(idPedidoGerado);
        when(pedidoRepository.findByIdPedido(idPedidoGerado)).thenReturn(null);

        // When
        Long resultado = efetuarPedidoUseCase.efetuar(solicitacao);

        // Then
        assertEquals(idPedidoGerado, resultado);
        verify(produtoRepository, times(1)).findById(1L);
        verify(pedidoRepository, times(1)).salvar(any(Pedido.class));
        verify(geradorIdPedidoService, times(1)).gerarId();
        // Verifica se o estoque foi atualizado com a quantidade desejada (2)
        verify(produtoRepository, times(1)).atualizarEstoque(1L, 2);
    }

    @Test
    void deveLancarExcecaoQuandoPedidoJaEstaEmAndamento() {
        // Given
        SolicitacaoEfetuarPedido solicitacao = criarSolicitacaoEfetuarPedido();
        Produto produtoDisponivel = criarProdutoEmEstoque(1L, 10);
        Long idPedidoGerado = 12345L;
        Pedido pedidoExistente = criarPedido();
        pedidoExistente.setStatusPedido("ATIVO");

        when(produtoRepository.findById(1L)).thenReturn(produtoDisponivel);
        when(geradorIdPedidoService.gerarId()).thenReturn(idPedidoGerado);
        when(pedidoRepository.findByIdPedido(idPedidoGerado)).thenReturn(pedidoExistente);

        // When & Then
        assertThrows(PedidoEmAndamentoException.class, () -> efetuarPedidoUseCase.efetuar(solicitacao));
        verify(pedidoRepository, never()).salvar(any(Pedido.class));
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        // Given
        SolicitacaoEfetuarPedido solicitacao = criarSolicitacaoEfetuarPedido();
        when(produtoRepository.findById(1L)).thenReturn(null);

        // When & Then
        assertThrows(ProdutoNaoEncontradoException.class, () -> efetuarPedidoUseCase.efetuar(solicitacao));
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {
        // Given
        SolicitacaoEfetuarPedido solicitacao = criarSolicitacaoEfetuarPedido(); // Solicita 2 unidades
        Produto produtoComEstoqueBaixo = criarProdutoEmEstoque(1L, 1); // Apenas 1 em estoque

        when(produtoRepository.findById(1L)).thenReturn(produtoComEstoqueBaixo);

        // When & Then
        assertThrows(ProdutoNaoDisponivelException.class, () -> efetuarPedidoUseCase.efetuar(solicitacao));
    }

    @Test
    void deveLancarExcecaoGenericaQuandoErroInesperadoAcontece() {
        // Given
        SolicitacaoEfetuarPedido solicitacao = criarSolicitacaoEfetuarPedido();
        when(produtoRepository.findById(1L)).thenThrow(new RuntimeException("Erro inesperado de banco de dados"));

        // When & Then
        assertThrows(RuntimeException.class, () -> efetuarPedidoUseCase.efetuar(solicitacao));
    }

    private SolicitacaoEfetuarPedido criarSolicitacaoEfetuarPedido() {
        // O cliente deseja 2 unidades do produto com ID 1
        Map<Long, Integer> produtosDesejados = Map.of(1L, 2);
        return new SolicitacaoEfetuarPedido("cliente-123", produtosDesejados, "transacao-abc");
    }

    private Produto criarProdutoEmEstoque(Long id, int quantidadeEmEstoque) {
        return new Produto(id, "Produto Teste", quantidadeEmEstoque, 10.00);
    }

    private Pedido criarPedido() {
        Produto produtoDoPedido = new Produto(1L, "Produto Teste", 2, 10.00);
        return new Pedido(12345L, "cliente-123", List.of(produtoDoPedido), "2023-10-01T10:00:00", "transacao-abc");
    }
}