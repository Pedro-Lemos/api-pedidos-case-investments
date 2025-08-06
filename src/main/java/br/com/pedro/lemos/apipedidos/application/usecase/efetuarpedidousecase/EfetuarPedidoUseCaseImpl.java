package br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase;

import br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository.PedidoRepository;
import br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository.ProdutoRepository;
import br.com.pedro.lemos.apipedidos.application.exception.ProdutoNaoDisponivelException;
import br.com.pedro.lemos.apipedidos.application.exception.ProdutoNaoEncontradoException;
import br.com.pedro.lemos.apipedidos.application.service.GeradorIdPedidoService;
import br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase.model.SolicitacaoEfetuarPedido;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import br.com.pedro.lemos.apipedidos.domain.entity.Produto;
import br.com.pedro.lemos.apipedidos.domain.utils.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EfetuarPedidoUseCaseImpl implements EfetuarPedidoUseCase {

    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;
    private final GeradorIdPedidoService geradorIdPedidoService;

    public EfetuarPedidoUseCaseImpl(ProdutoRepository produtoRepository, PedidoRepository pedidoRepository,
                                    GeradorIdPedidoService geradorIdPedidoService) {
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
        this.geradorIdPedidoService = geradorIdPedidoService;
    }

    @Override
    @Transactional
    public Long efetuar(SolicitacaoEfetuarPedido solicitacaoEfetuarPedido) {
        Map<Long, Integer> produtosSolicitados = solicitacaoEfetuarPedido.getProdutosSolicitados();
        List<Produto> produtosParaPedido = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : produtosSolicitados.entrySet()) {
            Long idProduto = entry.getKey();
            Integer quantidadeDesejada = entry.getValue();

            Produto produtoCadastrado = produtoRepository.findById(idProduto);
            if (produtoCadastrado == null) {
                throw new ProdutoNaoEncontradoException(idProduto);
            }

            if (produtoCadastrado.getQuantidadeProduto() < quantidadeDesejada) {
                throw new ProdutoNaoDisponivelException(produtoCadastrado.getNomeProduto());
            }

            Produto produtoDoPedido = new Produto(
                    produtoCadastrado.getIdProduto(),
                    produtoCadastrado.getNomeProduto(),
                    quantidadeDesejada,
                    produtoCadastrado.getPrecoUnitarioProduto()
            );
            produtosParaPedido.add(produtoDoPedido);
        }

        Long idPedido = geradorIdPedidoService.gerarId();

        String dataHoraAtual = LocalDateTime.now().format(DateUtils.FORMATTER_DATA_HORA_PT_BR);
        Pedido pedido = new Pedido(
                idPedido,
                solicitacaoEfetuarPedido.getCodigoIdentificacaoCliente(),
                produtosParaPedido,
                dataHoraAtual,
                solicitacaoEfetuarPedido.getTransactionId()
        );

        for (Produto produto : produtosParaPedido) {
            produtoRepository.atualizarEstoque(produto.getIdProduto(), produto.getQuantidadeProduto());
        }

        pedidoRepository.salvar(pedido);
        return idPedido;
    }
}