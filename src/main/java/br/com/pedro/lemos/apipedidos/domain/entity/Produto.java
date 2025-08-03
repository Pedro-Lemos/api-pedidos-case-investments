package br.com.pedro.lemos.apipedidos.domain.entity;

public class Produto {

    private Long idProduto;
    private String nomeProduto;
    private int quantidadeProduto;
    private Double precoUnitarioProduto;

    public Produto() {
    }

    public Produto(Long idProduto, String nomeProduto, int quantidadeProduto, Double precoUnitarioProduto) {
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.quantidadeProduto = quantidadeProduto;
        this.precoUnitarioProduto = precoUnitarioProduto;
    }

    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public int getQuantidadeProduto() {
        return quantidadeProduto;
    }

    public void setQuantidadeProduto(int quantidadeProduto) {
        this.quantidadeProduto = quantidadeProduto;
    }

    public Double getPrecoUnitarioProduto() {
        return precoUnitarioProduto;
    }

    public void setPrecoUnitarioProduto(Double precoUnitarioProduto) {
        this.precoUnitarioProduto = precoUnitarioProduto;
    }
}
