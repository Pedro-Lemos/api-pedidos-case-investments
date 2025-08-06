package br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository;

import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import br.com.pedro.lemos.apipedidos.domain.entity.Produto;
import br.com.pedro.lemos.apipedidos.domain.utils.DateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PedidoRepositoryImplTest {

    @TempDir
    Path tempDir;

    private PedidoRepositoryImpl repository;
    private File testFile;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        testFile = tempDir.resolve("pedidos-test.json").toFile();
        repository = new PedidoRepositoryImpl(testFile.getAbsolutePath());
    }

    @Test
    void deveCriarArquivoVazioQuandoNaoExistir() throws IOException {
        assertFalse(testFile.exists());
        repository.init();
        assertTrue(testFile.exists());
        String content = Files.readString(testFile.toPath());
        assertEquals("{}", content.replaceAll("\\s", ""));
    }

    @Test
    void deveSalvarEEncontrarPedido() {
        Pedido pedido = criarPedidoTeste();
        repository.salvar(pedido);

        Pedido pedidoEncontrado = repository.findByIdPedido(pedido.getIdPedido());

        assertNotNull(pedidoEncontrado);
        assertEquals(pedido.getIdPedido(), pedidoEncontrado.getIdPedido());
    }

    @Test
    void deveRetornarNuloSePedidoNaoExistir() {
        Pedido pedido = repository.findByIdPedido(999L);
        assertNull(pedido);
    }

    @Test
    void deveListarPedidosPorStatus() throws IOException {
        Pedido pedidoAtivo = criarPedidoTeste(1L, "ATIVO");
        Pedido pedidoInativo = criarPedidoTeste(2L, "INATIVO");
        Pedido outroPedidoAtivo = criarPedidoTeste(3L, "ATIVO");

        Map<Long, Pedido> pedidos = Map.of(
                pedidoAtivo.getIdPedido(), pedidoAtivo,
                pedidoInativo.getIdPedido(), pedidoInativo,
                outroPedidoAtivo.getIdPedido(), outroPedidoAtivo
        );
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(testFile, pedidos);

        List<Pedido> pedidosAtivos = repository.findByStatus("ATIVO");

        assertEquals(2, pedidosAtivos.size());
        assertTrue(pedidosAtivos.stream().allMatch(p -> p.getStatusPedido().equals("ATIVO")));
    }

    @Test
    void deveListarTodosOsPedidos() throws IOException {
        Pedido pedido1 = criarPedidoTeste(1L, "ATIVO");
        Pedido pedido2 = criarPedidoTeste(2L, "INATIVO");

        Map<Long, Pedido> pedidos = Map.of(
                pedido1.getIdPedido(), pedido1,
                pedido2.getIdPedido(), pedido2
        );
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(testFile, pedidos);

        List<Pedido> todosOsPedidos = repository.findAll();

        assertEquals(2, todosOsPedidos.size());
    }

    @Test
    void deveCarregarMapaVazioQuandoArquivoEstaVazio() throws IOException {
        Files.createFile(testFile.toPath());
        List<Pedido> pedidos = repository.findAll();
        assertTrue(pedidos.isEmpty());
    }

    @Test
    void deveLancarExcecaoAoCarregarArquivoComJSONInvalido() throws IOException {
        Files.writeString(testFile.toPath(), "[{]");
        assertThrows(RuntimeException.class, () -> repository.findAll());
    }

    private Pedido criarPedidoTeste() {
        return criarPedidoTeste(1L, "ATIVO");
    }

    private Pedido criarPedidoTeste(Long id, String status) {
        List<Produto> produtos = Arrays.asList(
                new Produto(1L, "Produto A", 1, 10.0),
                new Produto(2L, "Produto B", 2, 20.0)
        );

        Pedido pedido = new Pedido(
                id,
                UUID.randomUUID().toString(),
                produtos,
                LocalDateTime.now().format(DateUtils.FORMATTER_DATA_HORA_PT_BR),
                UUID.randomUUID().toString()
        );
        pedido.setStatusPedido(status);
        return pedido;
    }
}