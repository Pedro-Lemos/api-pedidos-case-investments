package br.com.pedro.lemos.apipedidos.domain.entity;

public enum StatusPedido {
    ATIVO("ATIVO"),
    INATIVO("INATIVO");

    private final String valor;

    StatusPedido(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
