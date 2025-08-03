package br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase.model;

import br.com.pedro.lemos.apipedidos.domain.entity.Produto;

import java.util.List;

public class SolicitacaoEfetuarPedido {

    private Long idPedido;
    private String codigoIdentificacaoCliente;
    private List<Produto> produto;
    private String transactionId;

    public SolicitacaoEfetuarPedido(Long idPedido, String codigoIdentificacaoCliente, List<Produto> produto, String transactionId) {
        this.idPedido = idPedido;
        this.codigoIdentificacaoCliente = codigoIdentificacaoCliente;
        this.produto = produto;
        this.transactionId = transactionId;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public String getCodigoIdentificacaoCliente() {
        return codigoIdentificacaoCliente;
    }

    public List<Produto> getProduto() {
        return produto;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
