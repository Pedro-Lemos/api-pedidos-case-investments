package br.com.pedro.lemos.apipedidos.application.service;

import br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository.PedidoRepository;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeradorIdPedidoServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;

    private GeradorIdPedidoServiceImpl geradorIdPedidoService;

    @BeforeEach
    void setUp() {
        geradorIdPedidoService = new GeradorIdPedidoServiceImpl(pedidoRepository);
    }

    @Test
    void deveIniciarContadorComZeroEGerarIdUmQuandoNaoExistemPedidos() {
        // Given
        when(pedidoRepository.findAll()).thenReturn(new ArrayList<>());
        geradorIdPedidoService.init();

        // When
        Long idGerado = geradorIdPedidoService.gerarId();

        // Then
        assertEquals(1L, idGerado);
    }

    @Test
    void deveIniciarContadorComMaxIdEGerarIdSequencial() {
        // Given
        Pedido pedido1 = new Pedido();
        pedido1.setIdPedido(10L);
        Pedido pedido2 = new Pedido();
        pedido2.setIdPedido(15L); // ID Máximo
        Pedido pedido3 = new Pedido();
        pedido3.setIdPedido(5L);

        when(pedidoRepository.findAll()).thenReturn(List.of(pedido1, pedido2, pedido3));
        geradorIdPedidoService.init(); // Simula a inicialização do PostConstruct

        // When
        Long primeiroIdGerado = geradorIdPedidoService.gerarId();
        Long segundoIdGerado = geradorIdPedidoService.gerarId();

        // Then
        assertEquals(16L, primeiroIdGerado); // 15 (max) + 1
        assertEquals(17L, segundoIdGerado); // 16 + 1
    }

    @Test
    void deveGerarIdsSequenciaisCorretamente() {
        // Given
        when(pedidoRepository.findAll()).thenReturn(new ArrayList<>());
        geradorIdPedidoService.init();

        // When & Then
        assertEquals(1L, geradorIdPedidoService.gerarId());
        assertEquals(2L, geradorIdPedidoService.gerarId());
        assertEquals(3L, geradorIdPedidoService.gerarId());
    }
}