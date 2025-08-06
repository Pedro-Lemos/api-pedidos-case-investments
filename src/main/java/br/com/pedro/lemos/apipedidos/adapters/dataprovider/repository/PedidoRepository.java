package br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository;

import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PedidoRepository {
    Pedido findByIdPedido(Long idPedido);

    void salvar(Pedido pedido);

    Page<Pedido> findByStatus(String status, Pageable pageable);

    List<Pedido> findAll();
}
