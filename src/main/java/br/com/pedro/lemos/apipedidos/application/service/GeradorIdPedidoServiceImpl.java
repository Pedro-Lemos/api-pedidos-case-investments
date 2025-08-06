package br.com.pedro.lemos.apipedidos.application.service;

import br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository.PedidoRepository;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class GeradorIdPedidoServiceImpl implements GeradorIdPedidoService {

    private final PedidoRepository pedidoRepository;
    private AtomicLong idCounter;

    public GeradorIdPedidoServiceImpl(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @PostConstruct
    public void init() {
        long maxId = pedidoRepository.findAll().stream()
                .mapToLong(Pedido::getIdPedido)
                .max()
                .orElse(0L);
        this.idCounter = new AtomicLong(maxId);
    }

    @Override
    public Long gerarId() {
        return idCounter.incrementAndGet();
    }
}