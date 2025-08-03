package br.com.pedro.lemos.apipedidos.application.usecase.listarpedidosusecase;

import br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository.PedidoRepository;
import br.com.pedro.lemos.apipedidos.application.exception.PedidosInativosException;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import br.com.pedro.lemos.apipedidos.domain.entity.StatusPedido;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarPedidosUseCaseImpl implements ListarPedidosUseCase {

    private final PedidoRepository pedidoRepository;

    public ListarPedidosUseCaseImpl(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public List<Pedido> listar() {
        List<Pedido> pedidosAtivos = pedidoRepository.findByStatus(StatusPedido.ATIVO.getValor());

        if (pedidosAtivos.isEmpty()) {
            throw new PedidosInativosException();
        }

        return pedidosAtivos;
    }
}
