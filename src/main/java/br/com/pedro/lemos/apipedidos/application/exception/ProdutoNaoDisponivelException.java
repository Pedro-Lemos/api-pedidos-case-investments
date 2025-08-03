package br.com.pedro.lemos.apipedidos.application.exception;

public class ProdutoNaoDisponivelException extends RuntimeException {
    public ProdutoNaoDisponivelException(String produto) {
        super("Não foi possível realizar o pedido. Quantidade do produto: " + produto + ", maior que o disponível em estoque.");
    }
}
