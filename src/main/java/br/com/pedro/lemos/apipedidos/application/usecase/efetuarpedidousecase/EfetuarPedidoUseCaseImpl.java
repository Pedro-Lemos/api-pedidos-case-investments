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

            // Cria uma nova instância de Produto para o pedido com os dados corretos
            Produto produtoDoPedido = new Produto(
                    produtoCadastrado.getIdProduto(),
                    produtoCadastrado.getNomeProduto(),
                    quantidadeDesejada, // Usa a quantidade desejada
                    produtoCadastrado.getPrecoUnitarioProduto()
            );
            produtosParaPedido.add(produtoDoPedido);
        }

        // 2. Gera o ID do pedido e verifica se já existe um pedido ativo com o mesmo ID
        Long idPedido = geradorIdPedidoService.gerarId();
        Pedido pedidoExistente = pedidoRepository.findByIdPedido(idPedido);
        if (pedidoExistente != null && pedidoExistente.getStatusPedido().equals("ATIVO")) {
            throw new PedidoEmAndamentoException(idPedido);
        }

        // 3. Cria o novo pedido
        String dataHoraAtual = LocalDateTime.now().format(DateUtils.FORMATTER_DATA_HORA_PT_BR);
        Pedido pedido = new Pedido(
                idPedido,
                solicitacaoEfetuarPedido.getCodigoIdentificacaoCliente(),
                produtosParaPedido, // Usa a lista de produtos criada
                dataHoraAtual,
                solicitacaoEfetuarPedido.getTransactionId()
        );

        // 4. Atualiza o estoque dos produtos
        for (Produto produto : produtosParaPedido) {
            produtoRepository.atualizarEstoque(produto.getIdProduto(), produto.getQuantidadeProduto());
        }

        // 5. Salva o pedido
        pedidoRepository.salvar(pedido);
        return idPedido;
    }
}