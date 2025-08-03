package br.com.pedro.lemos.apipedidos.application.exception;

public class ProdutoNaoEncontradoException extends RuntimeException {
    public ProdutoNaoEncontradoException(Long idProduto) {
        super("Não foi encontrado o produto: " + idProduto);
    }
}
