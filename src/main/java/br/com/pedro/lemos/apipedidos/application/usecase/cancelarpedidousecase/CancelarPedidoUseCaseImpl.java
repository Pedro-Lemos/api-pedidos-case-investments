package br.com.pedro.lemos.apipedidos.application.usecase.cancelarpedidousecase;

import br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository.PedidoRepository;
import br.com.pedro.lemos.apipedidos.application.exception.PedidoCanceladoException;
import br.com.pedro.lemos.apipedidos.application.exception.PedidoNaoEncontradoException;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import br.com.pedro.lemos.apipedidos.domain.entity.StatusPedido;
import br.com.pedro.lemos.apipedidos.domain.utils.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CancelarPedidoUseCaseImpl implements CancelarPedidoUseCase {

    private final PedidoRepository pedidoRepository;

    public CancelarPedidoUseCaseImpl(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    @Transactional
    public void cancelar(Long pedidoId, String motivoCancelamento) {
        Pedido pedido = pedidoRepository.findByIdPedido(pedidoId);

        if (pedido == null) {
            throw new PedidoNaoEncontradoException(pedidoId);
        }

        if (String.valueOf(StatusPedido.INATIVO).equals(pedido.getStatusPedido())) {
            throw new PedidoCanceladoException(pedidoId);
        }

        // Data/hora preenchida automaticamente pelo sistema
        String dataHoraAtual = LocalDateTime.now().format(DateUtils.FORMATTER_DATA_HORA_PT_BR);

        pedido.setStatusPedido(String.valueOf(StatusPedido.INATIVO));
        pedido.setMotivoCancelamento(motivoCancelamento);
        pedido.setDataHoraCancelamento(dataHoraAtual);

        // Salva o pedido atualizado
        pedidoRepository.salvar(pedido);
    }
}
