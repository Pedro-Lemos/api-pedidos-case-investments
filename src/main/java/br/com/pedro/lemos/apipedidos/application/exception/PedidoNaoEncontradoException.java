package br.com.pedro.lemos.apipedidos.application.exception;

public class PedidoNaoEncontradoException extends RuntimeException {
    public PedidoNaoEncontradoException(Long pedidoId) {
        super("Pedido com ID " + pedidoId + " não encontrado");
    }
}
