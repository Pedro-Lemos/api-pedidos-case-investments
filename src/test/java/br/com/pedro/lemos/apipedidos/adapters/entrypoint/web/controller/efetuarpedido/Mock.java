package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.efetuarpedido;

import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.efetuarpedido.request.EfetuarPedidoRequestV1;

import java.util.Arrays;
import java.util.List;

public final class Mock {

    private Mock() {

    }

    public static EfetuarPedidoRequestV1 criarRequestValido() {
        EfetuarPedidoRequestV1.ProdutoRequest produto = new EfetuarPedidoRequestV1.ProdutoRequest(
                1L, "Notebook Dell", 2, 2500.00
        );

        EfetuarPedidoRequestV1 request = new EfetuarPedidoRequestV1();
        request.setCodigoIdentificacaoCliente("CLI-001");
        request.setProdutos(List.of(produto));

        return request;
    }

    public static EfetuarPedidoRequestV1 criarRequestComMultiplosProdutos() {
        EfetuarPedidoRequestV1.ProdutoRequest produto1 = new EfetuarPedidoRequestV1.ProdutoRequest(
                1L, "Notebook Dell", 2, 2500.00
        );

        EfetuarPedidoRequestV1.ProdutoRequest produto2 = new EfetuarPedidoRequestV1.ProdutoRequest(
                2L, "Mouse Logitech", 3, 75.00
        );

        EfetuarPedidoRequestV1 request = new EfetuarPedidoRequestV1();
        request.setCodigoIdentificacaoCliente("CLI-002");
        request.setProdutos(Arrays.asList(produto1, produto2));

        return request;
    }

    public static EfetuarPedidoRequestV1 criarRequestComClientePersonalizado(String codigoCliente) {
        EfetuarPedidoRequestV1.ProdutoRequest produto = new EfetuarPedidoRequestV1.ProdutoRequest(
                3L, "Teclado Mecânico", 1, 350.00
        );

        EfetuarPedidoRequestV1 request = new EfetuarPedidoRequestV1();
        request.setCodigoIdentificacaoCliente(codigoCliente);
        request.setProdutos(List.of(produto));

        return request;
    }

    public static EfetuarPedidoRequestV1 criarRequestComProdutoPersonalizado(Long idProduto, String nomeProduto, int quantidade, Double preco) {
        EfetuarPedidoRequestV1.ProdutoRequest produto = new EfetuarPedidoRequestV1.ProdutoRequest(
                idProduto, nomeProduto, quantidade, preco
        );

        EfetuarPedidoRequestV1 request = new EfetuarPedidoRequestV1();
        request.setCodigoIdentificacaoCliente("CLI-TESTE");
        request.setProdutos(List.of(produto));

        return request;
    }

    public static EfetuarPedidoRequestV1.ProdutoRequest criarProdutoRequest(Long id, String nome, int quantidade, Double preco) {
        return new EfetuarPedidoRequestV1.ProdutoRequest(id, nome, quantidade, preco);
    }

    public static List<EfetuarPedidoRequestV1.ProdutoRequest> criarListaProdutosComuns() {
        return Arrays.asList(
                criarProdutoRequest(1L, "Notebook Dell", 2, 2500.00),
                criarProdutoRequest(2L, "Mouse Logitech", 3, 75.00),
                criarProdutoRequest(3L, "Teclado Mecânico", 1, 350.00)
        );
    }

}
