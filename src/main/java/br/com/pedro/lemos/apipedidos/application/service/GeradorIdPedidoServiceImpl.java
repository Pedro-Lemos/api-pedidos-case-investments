package br.com.pedro.lemos.apipedidos.application.service;

import br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository.PedidoRepository;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import br.com.pedro.lemos.apipedidos.domain.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class GeradorIdPedidoServiceImpl implements GeradorIdPedidoService {

    private final PedidoRepository pedidoRepository;

    public GeradorIdPedidoServiceImpl(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public Long gerarId() {
        LocalDate hoje = LocalDate.now();
        String dataFormatada = hoje.format(DateUtils.FORMATTER_DATA_ID);

        // Busca o próximo sequencial para o dia atual
        Long proximoSequencial = obterProximoSequencial(dataFormatada);

        String sequencial = String.format("%05d", proximoSequencial);
        return Long.valueOf(dataFormatada + sequencial);
    }

    private Long obterProximoSequencial(String dataFormatada) {
        // Busca todos os pedidos que começam com a data atual
        List<Pedido> pedidosHoje = pedidoRepository.findAll().stream()
                .filter(pedido -> pedido.getIdPedido().toString().startsWith(dataFormatada))
                .toList();

        if (pedidosHoje.isEmpty()) {
            return 1L;
        }

        // Extrai o maior sequencial do dia
        Long maiorSequencial = pedidosHoje.stream()
                .map(pedido -> {
                    String idStr = pedido.getIdPedido().toString();
                    String sequencialStr = idStr.substring(dataFormatada.length());
                    return Long.parseLong(sequencialStr);
                })
                .max(Long::compareTo)
                .orElse(0L);

        return maiorSequencial + 1;
    }
}