package br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase;

import br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository.PedidoRepository;
import br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository.ProdutoRepository;
import br.com.pedro.lemos.apipedidos.application.exception.PedidoEmAndamentoException;
import br.com.pedro.lemos.apipedidos.application.exception.ProdutoNaoDisponivelException;
import br.com.pedro.lemos.apipedidos.application.exception.ProdutoNaoEncontradoException;
import br.com.pedro.lemos.apipedidos.application.exception.DadosProdutoInconsistentesException;
import br.com.pedro.lemos.apipedidos.application.service.GeradorIdPedidoService;
import br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase.model.SolicitacaoEfetuarPedido;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import br.com.pedro.lemos.apipedidos.domain.entity.Produto;
import br.com.pedro.lemos.apipedidos.domain.utils.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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


        List<Produto> produtoPedido = solicitacaoEfetuarPedido.getProduto();
        for (Produto produto : produtoPedido) {
            Produto produtoCadastrado = produtoRepository.findById(produto.getIdProduto());
            if (produtoCadastrado == null) {
                throw new ProdutoNaoEncontradoException(produto.getIdProduto());
            }

            // Valida preco, nome e quantidade do produto
            validarConsistenciaProduto(produto, produtoCadastrado);

            int estoqueDisponivel = produtoRepository.getEstoqueDisponivel(produto.getIdProduto());

            if (estoqueDisponivel < produto.getQuantidadeProduto()) {
                throw new ProdutoNaoDisponivelException(produto.getNomeProduto());
            }
        }

        // Gera o id do Pedido
        Long idPedido = geradorIdPedidoService.gerarId();

        Pedido pedidoExistente = pedidoRepository.findByIdPedido(idPedido);
        if (pedidoExistente != null && pedidoExistente.getStatusPedido().equals("ATIVO")) {
            throw new PedidoEmAndamentoException(idPedido);
        }

        String dataHoraAtual = LocalDateTime.now().format(DateUtils.FORMATTER_DATA_HORA_PT_BR);
        Pedido pedido = new Pedido(
                idPedido,
                solicitacaoEfetuarPedido.getCodigoIdentificacaoCliente(),
                solicitacaoEfetuarPedido.getProduto(),
                dataHoraAtual,
                solicitacaoEfetuarPedido.getTransactionId()
        );

        for (Produto produto : produtoPedido) {
            produtoRepository.atualizarEstoque(produto.getIdProduto(), produto.getQuantidadeProduto());
        }

        pedidoRepository.salvar(pedido);
        return idPedido;
    }

    private void validarConsistenciaProduto(Produto produtoSolicitado, Produto produtoCadastrado) {
        // Validação de preço unitário
        if (!produtoSolicitado.getPrecoUnitarioProduto().equals(produtoCadastrado.getPrecoUnitarioProduto())) {
            throw new DadosProdutoInconsistentesException(
                    "Preço unitário informado (" + produtoSolicitado.getPrecoUnitarioProduto() +
                            ") não corresponde ao preço cadastrado (" + produtoCadastrado.getPrecoUnitarioProduto() +
                            ") para o produto: " + produtoCadastrado.getNomeProduto()
            );
        }

        // Validação de nome do produto
        if (!produtoSolicitado.getNomeProduto().equals(produtoCadastrado.getNomeProduto())) {
            throw new DadosProdutoInconsistentesException(
                    "Nome do produto informado (" + produtoSolicitado.getNomeProduto() +
                            ") não corresponde ao nome cadastrado (" + produtoCadastrado.getNomeProduto() +
                            ") para o ID: " + produtoSolicitado.getIdProduto()
            );
        }
    }
}
