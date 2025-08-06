package br.com.pedro.lemos.apipedidos.application.usecase.listarpedidosusecase;

import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarPedidosUseCase {
    Page<Pedido> listar(Pageable pageable);
}
