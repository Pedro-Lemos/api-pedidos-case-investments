package br.com.pedro.lemos.apipedidos.domain.entity;

public class Produto {

    private Long idProduto;
    private String nomeProduto; // renomear para apenas "nome"
    private int quantidadeProduto; // Sugiro renomear para "quantidadeDisponivel"
    private Double precoUnitarioProduto; // Aqui o tipo ideal seria BigDecimal e sugiro renomear apenas para "precoUnitario"

    public Produto() {
    }

    public Produto(Long idProduto, int quantidadeProduto) {
        this.idProduto = idProduto;
        this.quantidadeProduto = quantidadeProduto;
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

    public String getNomeProduto() {
        return nomeProduto;
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

}
