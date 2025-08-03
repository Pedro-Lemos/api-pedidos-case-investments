package br.com.pedro.lemos.apipedidos.application.usecase.buscarpedidousecase;

import br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository.PedidoRepository;
import br.com.pedro.lemos.apipedidos.application.exception.PedidoNaoEncontradoException;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BuscarPedidoUseCaseImplTest {

    private final PedidoRepository pedidoRepository = mock(PedidoRepository.class);
    private final BuscarPedidoUseCaseImpl buscarPedidoUseCase = new BuscarPedidoUseCaseImpl(pedidoRepository);

    @Test
    void deveRetornarPedidoComSucesso() {
        Pedido pedido = new Pedido();
        when(pedidoRepository.findByIdPedido(1L)).thenReturn(pedido);

        Pedido resultado = buscarPedidoUseCase.buscar(1L);

        assertEquals(pedido, resultado);
        verify(pedidoRepository, times(1)).findByIdPedido(1L);
    }

    @Test
    void lancaPedidoNaoEncontradoExceptionQuandoIdInvalidoFornecido() {
        when(pedidoRepository.findByIdPedido(2L)).thenReturn(null);

        assertThrows(PedidoNaoEncontradoException.class, () -> buscarPedidoUseCase.buscar(2L));
        verify(pedidoRepository, times(1)).findByIdPedido(2L);
    }

}