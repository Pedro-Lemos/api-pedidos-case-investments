package br.com.pedro.lemos.apipedidos.application.exception;

public class PedidoCanceladoException extends RuntimeException {
    public PedidoCanceladoException(Long pedidoId) {
        super("O pedido " + pedidoId + " já está cancelado");
    }
}
