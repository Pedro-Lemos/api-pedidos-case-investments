package br.com.pedro.lemos.apipedidos.application.exception;

public class DadosProdutoInconsistentesException extends RuntimeException {
  public DadosProdutoInconsistentesException(String mensagem) {
    super(mensagem);
  }
}