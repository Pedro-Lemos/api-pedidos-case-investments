package br.com.pedro.lemos.apipedidos.application.usecase.buscarpedidousecase;

import br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository.PedidoRepository;
import br.com.pedro.lemos.apipedidos.application.exception.PedidoNaoEncontradoException;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import org.springframework.stereotype.Service;

@Service
public class BuscarPedidoUseCaseImpl implements BuscarPedidoUseCase {

    private final PedidoRepository pedidoRepository;

    public BuscarPedidoUseCaseImpl(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public Pedido buscar(Long pedidoId) {
        Pedido pedido = pedidoRepository.findByIdPedido(pedidoId);

        if (pedido == null) {
            throw new PedidoNaoEncontradoException(pedidoId);
        }

        return pedido;
    }
}
