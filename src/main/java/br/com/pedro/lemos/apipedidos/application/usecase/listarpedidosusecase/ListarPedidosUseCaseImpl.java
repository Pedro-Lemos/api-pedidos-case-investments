package br.com.pedro.lemos.apipedidos.application.usecase.listarpedidosusecase;

import br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository.PedidoRepository;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import br.com.pedro.lemos.apipedidos.domain.entity.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListarPedidosUseCaseImpl implements ListarPedidosUseCase {

    private final PedidoRepository pedidoRepository;

    public ListarPedidosUseCaseImpl(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public Page<Pedido> listar(Pageable pageable) {
        return pedidoRepository.findByStatus(String.valueOf(StatusPedido.ATIVO), pageable);
    }
}