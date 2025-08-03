package br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository;

import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;

import java.util.List;

public interface PedidoRepository {
    Pedido findByIdPedido(Long idPedido);

    void salvar(Pedido pedido);

    List<Pedido> findByStatus(String status);
}
