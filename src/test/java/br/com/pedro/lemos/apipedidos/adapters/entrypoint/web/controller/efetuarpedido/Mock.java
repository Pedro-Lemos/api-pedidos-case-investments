package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.efetuarpedido;

import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.efetuarpedido.request.EfetuarPedidoRequestV1;

import java.util.List;

public final class Mock {

    private Mock() {
    }

    public static EfetuarPedidoRequestV1 criarRequestValido() {
        EfetuarPedidoRequestV1.ProdutoRequest produtoRequest = new EfetuarPedidoRequestV1.ProdutoRequest(1L, 1);
        return new EfetuarPedidoRequestV1("CLI-001", List.of(produtoRequest));
    }

    public static EfetuarPedidoRequestV1 criarRequestComMultiplosProdutos() {
        EfetuarPedidoRequestV1.ProdutoRequest produto1 = new EfetuarPedidoRequestV1.ProdutoRequest(1L, 2);
        EfetuarPedidoRequestV1.ProdutoRequest produto2 = new EfetuarPedidoRequestV1.ProdutoRequest(2L, 3);
        return new EfetuarPedidoRequestV1("CLI-002", List.of(produto1, produto2));
    }

    public static EfetuarPedidoRequestV1 criarRequestInvalido() {
        return new EfetuarPedidoRequestV1(null, null);
    }
}