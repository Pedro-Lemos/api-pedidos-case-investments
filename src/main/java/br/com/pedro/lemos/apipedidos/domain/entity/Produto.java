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

    // REMOVA todos os métodos set() e deixe somente os que for usar.
    // Penso que talvez só o método de atualizar quantidade seja necessário
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
