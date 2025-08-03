package br.com.pedro.lemos.apipedidos.application.usecase.listarpedidosusecase;

import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;

import java.util.List;

public interface ListarPedidosUseCase {
    List<Pedido> listar();
}
