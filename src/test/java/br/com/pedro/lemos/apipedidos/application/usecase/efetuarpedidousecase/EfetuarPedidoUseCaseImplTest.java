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
        SolicitacaoEfetuarPedido solicitacao = criarSolicitacaoEfetuarPedido();
        Produto produto = criarProduto();
        Long idPedidoGerado = 1L;

        when(produtoRepository.findById(produto.getIdProduto())).thenReturn(produto);
        when(produtoRepository.getEstoqueDisponivel(produto.getIdProduto())).thenReturn(10);
        when(geradorIdPedidoService.gerarId()).thenReturn(idPedidoGerado);
        when(pedidoRepository.findByIdPedido(idPedidoGerado)).thenReturn(null);

        Long resultado = efetuarPedidoUseCase.efetuar(solicitacao);

        assertEquals(idPedidoGerado, resultado);
        verify(produtoRepository, times(1)).atualizarEstoque(produto.getIdProduto(), produto.getQuantidadeProduto());
        verify(pedidoRepository, times(1)).salvar(any(Pedido.class));
        verify(geradorIdPedidoService, times(1)).gerarId();
    }

    @Test
    void deveLancarExcecaoQuandoPedidoJaEstaEmAndamento() {
        SolicitacaoEfetuarPedido solicitacao = criarSolicitacaoEfetuarPedido();
        Produto produto = criarProduto();
        Long idPedidoGerado = 1L;
        Pedido pedidoExistente = criarPedido();
        pedidoExistente.setStatusPedido("ATIVO");

        // Mock para passar na validação do produto primeiro
        when(produtoRepository.findById(produto.getIdProduto())).thenReturn(produto);
        when(produtoRepository.getEstoqueDisponivel(produto.getIdProduto())).thenReturn(10);
        when(geradorIdPedidoService.gerarId()).thenReturn(idPedidoGerado);
        when(pedidoRepository.findByIdPedido(idPedidoGerado)).thenReturn(pedidoExistente);

        assertThrows(PedidoEmAndamentoException.class, () -> efetuarPedidoUseCase.efetuar(solicitacao));
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        SolicitacaoEfetuarPedido solicitacao = criarSolicitacaoEfetuarPedido();
        Produto produto = criarProduto();

        when(produtoRepository.findById(produto.getIdProduto())).thenReturn(null);

        assertThrows(ProdutoNaoEncontradoException.class, () -> efetuarPedidoUseCase.efetuar(solicitacao));
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {
        SolicitacaoEfetuarPedido solicitacao = criarSolicitacaoEfetuarPedido();
        Produto produto = criarProduto();

        when(produtoRepository.findById(produto.getIdProduto())).thenReturn(produto);
        when(produtoRepository.getEstoqueDisponivel(produto.getIdProduto())).thenReturn(0);

        assertThrows(ProdutoNaoDisponivelException.class, () -> efetuarPedidoUseCase.efetuar(solicitacao));
    }

    @Test
    void deveLancarExcecaoGenericaQuandoErroInesperadoAcontece() {
        SolicitacaoEfetuarPedido solicitacao = criarSolicitacaoEfetuarPedido();
        Produto produto = criarProduto();

        when(produtoRepository.findById(produto.getIdProduto())).thenThrow(new RuntimeException("Erro inesperado"));

        assertThrows(RuntimeException.class, () -> efetuarPedidoUseCase.efetuar(solicitacao));
    }

    private SolicitacaoEfetuarPedido criarSolicitacaoEfetuarPedido() {
        Produto produto = criarProduto();
        return new SolicitacaoEfetuarPedido("123", List.of(produto), "transacao123");
    }

    private Produto criarProduto() {
        return new Produto(1L, "Produto Teste", 5, 10.00);
    }

    private Pedido criarPedido() {
        return new Pedido(1L, "123", List.of(criarProduto()), "2023-10-01T10:00:00", "transacao123");
    }
}