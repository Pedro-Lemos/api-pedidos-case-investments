package br.com.pedro.lemos.apipedidos.application.usecase.listarpedidosusecase;

import br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository.PedidoRepository;
import br.com.pedro.lemos.apipedidos.application.exception.PedidosInativosException;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import br.com.pedro.lemos.apipedidos.domain.entity.StatusPedido;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListarPedidosUseCaseImplTest {

    private final PedidoRepository pedidoRepository = mock(PedidoRepository.class);
    private final ListarPedidosUseCaseImpl listarPedidosUseCase = new ListarPedidosUseCaseImpl(pedidoRepository);

    @Test
    void retornaListaDePedidosAtivosQuandoExistemPedidosAtivos() {
        List<Pedido> pedidosAtivos = List.of(new Pedido(), new Pedido());
        when(pedidoRepository.findByStatus(StatusPedido.ATIVO.getValor())).thenReturn(pedidosAtivos);

        List<Pedido> resultado = listarPedidosUseCase.listar();

        assertEquals(pedidosAtivos, resultado);
        verify(pedidoRepository, times(1)).findByStatus(StatusPedido.ATIVO.getValor());
    }

    @Test
    void lancaPedidosInativosExceptionQuandoNaoExistemPedidosAtivos() {
        when(pedidoRepository.findByStatus(StatusPedido.ATIVO.getValor())).thenReturn(Collections.emptyList());

        assertThrows(PedidosInativosException.class, listarPedidosUseCase::listar);
        verify(pedidoRepository, times(1)).findByStatus(StatusPedido.ATIVO.getValor());
    }


}