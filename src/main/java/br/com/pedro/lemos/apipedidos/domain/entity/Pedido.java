package br.com.pedro.lemos.apipedidos.domain.entity;

import java.util.List;

public class Pedido {
    private Long idPedido;
    private String codigoIdentificacaoCliente;
    private String statusPedido;
    private List<Produto> descricaoProdutos;
    private String dataHoraCriacaoPedido;
    private String transactionId;
    private String motivoCancelamento;
    private String dataHoraCancelamento;

    public Pedido() {

    }

    public Pedido(Long idPedido, String codigoIdentificacaoCliente, List<Produto> descricaoProdutos, String dataHoraCriacaoPedido, String transactionId) {
        this.idPedido = idPedido;
        this.codigoIdentificacaoCliente = codigoIdentificacaoCliente;
        this.statusPedido = StatusPedido.ATIVO.getValor();
        this.descricaoProdutos = descricaoProdutos;
        this.dataHoraCriacaoPedido = dataHoraCriacaoPedido;
        this.transactionId = transactionId;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public String getStatusPedido() {
        return statusPedido;
    }

    public String getCodigoIdentificacaoCliente() {
        return codigoIdentificacaoCliente;
    }

    public void setCodigoIdentificacaoCliente(String codigoIdentificacaoCliente) {
        this.codigoIdentificacaoCliente = codigoIdentificacaoCliente;
    }

    public void setStatusPedido(String statusPedido) {
        this.statusPedido = statusPedido;
    }

    public List<Produto> getDescricaoProdutos() {
        return descricaoProdutos;
    }

    public void setDescricaoProdutos(List<Produto> descricaoProdutos) {
        this.descricaoProdutos = descricaoProdutos;
    }

    public String getDataHoraCriacaoPedido() {
        return dataHoraCriacaoPedido;
    }

    public void setDataHoraCriacaoPedido(String dataHoraCriacaoPedido) {
        this.dataHoraCriacaoPedido = dataHoraCriacaoPedido;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public String getDataHoraCancelamento() {
        return dataHoraCancelamento;
    }

    public void setDataHoraCancelamento(String dataHoraCancelamento) {
        this.dataHoraCancelamento = dataHoraCancelamento;
    }
}
