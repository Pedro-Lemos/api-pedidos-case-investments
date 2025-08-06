package br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase.model;

import java.util.Map;

public class SolicitacaoEfetuarPedido {

    private final String codigoIdentificacaoCliente;
    private final Map<Long, Integer> produtosDesejados;
    private final String transactionId;

    public SolicitacaoEfetuarPedido(String codigoIdentificacaoCliente, Map<Long, Integer> produtosDesejados, String transactionId) {
        this.codigoIdentificacaoCliente = codigoIdentificacaoCliente;
        this.produtosDesejados = produtosDesejados;
        this.transactionId = transactionId;
    }

    public String getCodigoIdentificacaoCliente() {
        return codigoIdentificacaoCliente;
    }

    public Map<Long, Integer> getProdutosSolicitados() {
        return produtosDesejados;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
