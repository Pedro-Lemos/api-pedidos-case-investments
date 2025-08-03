package br.com.pedro.lemos.apipedidos.application.usecase.cancelarpedidousecase;

import br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository.PedidoRepository;
import br.com.pedro.lemos.apipedidos.application.exception.PedidoCanceladoException;
import br.com.pedro.lemos.apipedidos.application.exception.PedidoNaoEncontradoException;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import br.com.pedro.lemos.apipedidos.domain.entity.StatusPedido;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CancelarPedidoUseCaseImplTest {

    private final PedidoRepository pedidoRepository = mock(PedidoRepository.class);
    private final CancelarPedidoUseCaseImpl cancelarPedidoUseCase = new CancelarPedidoUseCaseImpl(pedidoRepository);

    @Test
    void deveCancelarPedidoComSucesso() {
        Long pedidoId = 123L;
        String motivoCancelamento = "Pedido incorreto";
        Pedido pedido = new Pedido();
        pedido.setIdPedido(pedidoId);
        pedido.setStatusPedido(StatusPedido.ATIVO.getValor());

        when(pedidoRepository.findByIdPedido(pedidoId)).thenReturn(pedido);

        cancelarPedidoUseCase.cancelar(pedidoId, motivoCancelamento);

        verify(pedidoRepository, times(1)).findByIdPedido(pedidoId);
        verify(pedidoRepository, times(1)).salvar(any(Pedido.class));
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNaoEncontrado() {
        Long pedidoId = 999L;
        String motivoCancelamento = "Pedido incorreto";

        when(pedidoRepository.findByIdPedido(pedidoId)).thenReturn(null);

        assertThrows(PedidoNaoEncontradoException.class, () -> {
            cancelarPedidoUseCase.cancelar(pedidoId, motivoCancelamento);
        });

        verify(pedidoRepository, times(1)).findByIdPedido(pedidoId);
        verify(pedidoRepository, never()).salvar(any(Pedido.class));
    }

    @Test
    void deveLancarExcecaoQuandoPedidoJaCancelado() {
        Long pedidoId = 456L;
        String motivoCancelamento = "Pedido incorreto";
        Pedido pedido = new Pedido();
        pedido.setIdPedido(pedidoId);
        pedido.setStatusPedido(StatusPedido.INATIVO.getValor());

        when(pedidoRepository.findByIdPedido(pedidoId)).thenReturn(pedido);

        assertThrows(PedidoCanceladoException.class, () -> {
            cancelarPedidoUseCase.cancelar(pedidoId, motivoCancelamento);
        });

        verify(pedidoRepository, times(1)).findByIdPedido(pedidoId);
        verify(pedidoRepository, never()).salvar(any(Pedido.class));
    }

    @Test
    void devePreencherCamposDeFormaDeterministicaCancelamento() {
        Long pedidoId = 789L;
        String motivoCancelamento = "Cliente desistiu";
        Pedido pedido = new Pedido();
        pedido.setIdPedido(pedidoId);
        pedido.setStatusPedido(StatusPedido.ATIVO.getValor());

        when(pedidoRepository.findByIdPedido(pedidoId)).thenReturn(pedido);

        cancelarPedidoUseCase.cancelar(pedidoId, motivoCancelamento);

        verify(pedidoRepository).salvar(argThat(pedidoSalvo ->
                StatusPedido.INATIVO.getValor().equals(pedidoSalvo.getStatusPedido()) &&
                        motivoCancelamento.equals(pedidoSalvo.getMotivoCancelamento()) &&
                        pedidoSalvo.getDataHoraCancelamento() != null
        ));
    }
}