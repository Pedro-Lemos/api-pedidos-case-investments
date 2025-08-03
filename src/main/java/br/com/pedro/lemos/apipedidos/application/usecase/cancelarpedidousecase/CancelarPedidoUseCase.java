package br.com.pedro.lemos.apipedidos.application.usecase.cancelarpedidousecase;

public interface CancelarPedidoUseCase {
    void cancelar(Long pedidoId, String motivoCancelamento);
}
