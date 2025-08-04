package br.com.pedro.lemos.apipedidos.application.service;

import br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository.PedidoRepository;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import br.com.pedro.lemos.apipedidos.domain.entity.Produto;
import br.com.pedro.lemos.apipedidos.domain.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeradorIdPedidoServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private GeradorIdPedidoServiceImpl geradorIdPedidoService;

    @Test
    void deveGerarPrimeiroIdDoDiaQuandoNaoExistemPedidos() {
        when(pedidoRepository.findAll()).thenReturn(new ArrayList<>());

        Long idGerado = geradorIdPedidoService.gerarId();

        String dataHoje = LocalDate.now().format(DateUtils.FORMATTER_DATA_ID);
        Long idEsperado = Long.valueOf(dataHoje + "00001");

        assertEquals(idEsperado, idGerado);
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void deveGerarSegundoIdDoDiaQuandoJaExisteUmPedido() {
        String dataHoje = LocalDate.now().format(DateUtils.FORMATTER_DATA_ID);
        Long idExistente = Long.valueOf(dataHoje + "00001");

        Pedido pedidoExistente = criarPedido(idExistente);
        when(pedidoRepository.findAll()).thenReturn(List.of(pedidoExistente));

        Long idGerado = geradorIdPedidoService.gerarId();

        Long idEsperado = Long.valueOf(dataHoje + "00002");
        assertEquals(idEsperado, idGerado);
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void deveGerarProximoIdConsiderandoMultiplosPedidosDoMesmoDia() {
        String dataHoje = LocalDate.now().format(DateUtils.FORMATTER_DATA_ID);

        List<Pedido> pedidosExistentes = Arrays.asList(
                criarPedido(Long.valueOf(dataHoje + "00001")),
                criarPedido(Long.valueOf(dataHoje + "00003")),
                criarPedido(Long.valueOf(dataHoje + "00002"))
        );

        when(pedidoRepository.findAll()).thenReturn(pedidosExistentes);

        Long idGerado = geradorIdPedidoService.gerarId();

        Long idEsperado = Long.valueOf(dataHoje + "00004");
        assertEquals(idEsperado, idGerado);
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void deveIgnorarPedidosDeOutrosDiasAoGerarId() {
        String dataHoje = LocalDate.now().format(DateUtils.FORMATTER_DATA_ID);
        String dataOntem = LocalDate.now().minusDays(1).format(DateUtils.FORMATTER_DATA_ID);

        List<Pedido> pedidosExistentes = Arrays.asList(
                criarPedido(Long.valueOf(dataOntem + "00001")),
                criarPedido(Long.valueOf(dataOntem + "00002")),
                criarPedido(Long.valueOf(dataHoje + "00001"))
        );

        when(pedidoRepository.findAll()).thenReturn(pedidosExistentes);

        Long idGerado = geradorIdPedidoService.gerarId();

        Long idEsperado = Long.valueOf(dataHoje + "00002");
        assertEquals(idEsperado, idGerado);
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void deveGerarIdCorretamenteQuandoExistemPedidosDeVariasDatas() {
        String dataHoje = LocalDate.now().format(DateUtils.FORMATTER_DATA_ID);
        String dataOntem = LocalDate.now().minusDays(1).format(DateUtils.FORMATTER_DATA_ID);
        String dataAmanha = LocalDate.now().plusDays(1).format(DateUtils.FORMATTER_DATA_ID);

        List<Pedido> pedidosExistentes = Arrays.asList(
                criarPedido(Long.valueOf(dataOntem + "00001")),
                criarPedido(Long.valueOf(dataHoje + "00001")),
                criarPedido(Long.valueOf(dataHoje + "00002")),
                criarPedido(Long.valueOf(dataAmanha + "00001"))
        );

        when(pedidoRepository.findAll()).thenReturn(pedidosExistentes);

        Long idGerado = geradorIdPedidoService.gerarId();

        Long idEsperado = Long.valueOf(dataHoje + "00003");
        assertEquals(idEsperado, idGerado);
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void deveGerarIdComSequencialAltoCorretamente() {
        String dataHoje = LocalDate.now().format(DateUtils.FORMATTER_DATA_ID);

        List<Pedido> pedidosExistentes = new ArrayList<>();
        for (int i = 1; i <= 99; i++) {
            String sequencial = String.format("%05d", i);
            pedidosExistentes.add(criarPedido(Long.valueOf(dataHoje + sequencial)));
        }

        when(pedidoRepository.findAll()).thenReturn(pedidosExistentes);

        Long idGerado = geradorIdPedidoService.gerarId();

        Long idEsperado = Long.valueOf(dataHoje + "00100");
        assertEquals(idEsperado, idGerado);
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void deveReiniciarSequencialParaNovoDia() {
        String dataOntem = LocalDate.now().minusDays(1).format(DateUtils.FORMATTER_DATA_ID);
        String dataHoje = LocalDate.now().format(DateUtils.FORMATTER_DATA_ID);

        List<Pedido> pedidosExistentes = Arrays.asList(
                criarPedido(Long.valueOf(dataOntem + "00050")),
                criarPedido(Long.valueOf(dataOntem + "00051"))
        );

        when(pedidoRepository.findAll()).thenReturn(pedidosExistentes);

        Long idGerado = geradorIdPedidoService.gerarId();

        Long idEsperado = Long.valueOf(dataHoje + "00001");
        assertEquals(idEsperado, idGerado);
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void deveManterFormatacaoCorretaDoId() {
        when(pedidoRepository.findAll()).thenReturn(new ArrayList<>());

        Long idGerado = geradorIdPedidoService.gerarId();

        String idString = idGerado.toString();
        assertEquals(13, idString.length()); // 8 dígitos da data + 5 do sequencial
        assertTrue(idString.endsWith("00001"));
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void deveConsiderarApenasIdsDaDataAtualParaSequencial() {
        String dataHoje = LocalDate.now().format(DateUtils.FORMATTER_DATA_ID);
        String outraData = "20231201";

        List<Pedido> pedidosExistentes = Arrays.asList(
                criarPedido(Long.valueOf(outraData + "00010")),
                criarPedido(Long.valueOf(dataHoje + "00005")),
                criarPedido(999999L), // ID que não segue o padrão de data
                criarPedido(Long.valueOf(dataHoje + "00003"))
        );

        when(pedidoRepository.findAll()).thenReturn(pedidosExistentes);

        Long idGerado = geradorIdPedidoService.gerarId();

        Long idEsperado = Long.valueOf(dataHoje + "00006");
        assertEquals(idEsperado, idGerado);
        verify(pedidoRepository, times(1)).findAll();
    }

    private Pedido criarPedido(Long idPedido) {
        Produto produto = new Produto(1L, "Produto Teste", 1, 10.0);
        return new Pedido(
                idPedido,
                "CLI-001",
                List.of(produto),
                "04-08-2025 17:00:00",
                "TXN-123"
        );
    }
}