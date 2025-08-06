package br.com.pedro.lemos.apipedidos.application.usecase.listarpedidosusecase;

import br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository.PedidoRepository;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import br.com.pedro.lemos.apipedidos.domain.entity.StatusPedido;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ListarPedidosUseCaseImplTest {

    private final PedidoRepository pedidoRepository = mock(PedidoRepository.class);
    private final ListarPedidosUseCaseImpl listarPedidosUseCase = new ListarPedidosUseCaseImpl(pedidoRepository);

    @Test
    void retornaPaginaDePedidosAtivosQuandoExistemPedidosAtivos() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Pedido> pedidosAtivos = List.of(new Pedido(), new Pedido());
        Page<Pedido> pageDePedidos = new PageImpl<>(pedidosAtivos, pageable, pedidosAtivos.size());

        when(pedidoRepository.findByStatus(String.valueOf(StatusPedido.ATIVO), pageable)).thenReturn(pageDePedidos);

        Page<Pedido> resultado = listarPedidosUseCase.listar(pageable);

        assertEquals(pageDePedidos, resultado);
        verify(pedidoRepository, times(1)).findByStatus(String.valueOf(StatusPedido.ATIVO), pageable);
    }

    @Test
    void retornaPaginaVaziaQuandoNaoExistemPedidosAtivos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pedido> pageVazia = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(pedidoRepository.findByStatus(String.valueOf(StatusPedido.ATIVO), pageable)).thenReturn(pageVazia);

        Page<Pedido> resultado = listarPedidosUseCase.listar(pageable);

        assertTrue(resultado.isEmpty());
        verify(pedidoRepository, times(1)).findByStatus(String.valueOf(StatusPedido.ATIVO), pageable);
    }
}