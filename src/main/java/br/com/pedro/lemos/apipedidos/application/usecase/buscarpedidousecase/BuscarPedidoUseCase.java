package br.com.pedro.lemos.apipedidos.application.usecase.buscarpedidousecase;

import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;

public interface BuscarPedidoUseCase {
    Pedido buscar(Long pedidoId);
}
