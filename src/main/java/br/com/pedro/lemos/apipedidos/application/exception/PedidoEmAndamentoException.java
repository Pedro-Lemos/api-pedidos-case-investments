package br.com.pedro.lemos.apipedidos.application.exception;

public class PedidoEmAndamentoException extends RuntimeException {
    public PedidoEmAndamentoException(Long idPedido) {
        super("Existe um pedido em andamento para o idPedido: " + idPedido);
    }
}
