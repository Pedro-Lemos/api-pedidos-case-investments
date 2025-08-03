package br.com.pedro.lemos.apipedidos.application.exception;

public class PedidosInativosException extends RuntimeException {
    public PedidosInativosException() {
        super("Não há pedidos ativos na base.");
    }
}
