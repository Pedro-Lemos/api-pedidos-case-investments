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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PedidoRepositoryImplTest {

    @TempDir
    Path tempDir;

    private PedidoRepositoryImpl repository;
    private String filePath;
    private ObjectMapper objectMapper;
    private String codigoCliente = UUID.randomUUID().toString();
    private String transactionId = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        filePath = tempDir.resolve("pedidos-test.json").toString();
        repository = new PedidoRepositoryImpl(filePath);
        objectMapper = new ObjectMapper();
    }

    @Test
    void deveCriarArquivoVazioQuandoNaoExistir() {
        
        repository.init();

        
        File file = new File(filePath);
        assertTrue(file.exists());
        assertTrue(file.length() >= 2); // pelo menos "[]"
    }

    @Test
    void deveSalvarPedidoCorretamente() {
        
        repository.init();
        Pedido pedido = criarPedidoTeste();

        
        repository.salvar(pedido);

        
        Pedido pedidoSalvo = repository.findByIdPedido(1L);
        assertNotNull(pedidoSalvo);
        assertEquals(1L, pedidoSalvo.getIdPedido());
        assertEquals(codigoCliente, pedidoSalvo.getCodigoIdentificacaoCliente());
        assertEquals("ATIVO", pedidoSalvo.getStatusPedido());
        assertEquals(transactionId, pedidoSalvo.getTransactionId());
        assertEquals(2, pedidoSalvo.getDescricaoProdutos().size());
    }

    @Test
    void deveAtualizarPedidoExistente() {
        
        repository.init();
        Pedido pedidoOriginal = criarPedidoTeste();
        repository.salvar(pedidoOriginal);

        pedidoOriginal.setStatusPedido("CANCELADO");
        repository.salvar(pedidoOriginal);

        
        Pedido pedidoAtualizado = repository.findByIdPedido(1L);
        assertNotNull(pedidoAtualizado);
        assertEquals("CANCELADO", pedidoAtualizado.getStatusPedido());
        assertEquals(codigoCliente, pedidoAtualizado.getCodigoIdentificacaoCliente());
    }

    @Test
    void deveRetornarNullQuandoPedidoNaoExistir() {
        
        repository.init();

        
        Pedido pedido = repository.findByIdPedido(999L);

        
        assertNull(pedido);
    }

    @Test
    void deveBuscarPedidosPorStatus() {
        
        repository.init();
        Pedido pedidoAtivo1 = criarPedidoTeste(1L, "ATIVO");
        Pedido pedidoAtivo2 = criarPedidoTeste(2L, "ATIVO");
        Pedido pedidoCancelado = criarPedidoTeste(3L, "CANCELADO");

        repository.salvar(pedidoAtivo1);
        repository.salvar(pedidoAtivo2);
        repository.salvar(pedidoCancelado);

        
        List<Pedido> pedidosAtivos = repository.findByStatus("ATIVO");
        List<Pedido> pedidosCancelados = repository.findByStatus("CANCELADO");

        
        assertEquals(2, pedidosAtivos.size());
        assertEquals(1, pedidosCancelados.size());
        assertTrue(pedidosAtivos.stream().allMatch(p -> "ATIVO".equals(p.getStatusPedido())));
        assertTrue(pedidosCancelados.stream().allMatch(p -> "CANCELADO".equals(p.getStatusPedido())));
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverPedidosComStatus() {
        
        repository.init();

        
        List<Pedido> pedidos = repository.findByStatus("INEXISTENTE");

        
        assertTrue(pedidos.isEmpty());
    }

    @Test
    void deveSalvarMultiplosPedidos() {
        
        repository.init();
        Pedido pedido1 = criarPedidoTeste(1L, "ATIVO");
        Pedido pedido2 = criarPedidoTeste(2L, "CANCELADO");
        Pedido pedido3 = criarPedidoTeste(3L, "ATIVO");

        
        repository.salvar(pedido1);
        repository.salvar(pedido2);
        repository.salvar(pedido3);

        
        assertNotNull(repository.findByIdPedido(1L));
        assertNotNull(repository.findByIdPedido(2L));
        assertNotNull(repository.findByIdPedido(3L));
    }

    @Test
    void deveManterDadosAposRecriarRepository() {
        
        repository.init();
        Pedido pedido = criarPedidoTeste();
        repository.salvar(pedido);

        PedidoRepositoryImpl novoRepository = new PedidoRepositoryImpl(filePath);

        
        Pedido pedidoCarregado = novoRepository.findByIdPedido(1L);
        assertNotNull(pedidoCarregado);
        assertEquals(codigoCliente, pedidoCarregado.getCodigoIdentificacaoCliente());
    }

    @Test
    void deveCarregarListaVaziaQuandoArquivoEstaVazio() throws IOException {
        
        Files.createFile(Path.of(filePath));

        
        List<Pedido> pedidos = repository.findByStatus("ATIVO");

        
        assertTrue(pedidos.isEmpty());
    }

    @Test
    void deveCarregarListaVaziaQuandoArquivoTemJSONInvalido() throws IOException {
        
        Files.write(Path.of(filePath), "json inválido".getBytes());

        
        List<Pedido> pedidos = repository.findByStatus("ATIVO");

        
        assertTrue(pedidos.isEmpty());
    }

    private Pedido criarPedidoTeste() {
        return criarPedidoTeste(1L, "ATIVO");
    }

    private Pedido criarPedidoTeste(Long id, String status) {
        List<Produto> produtos = Arrays.asList(
                new Produto(1L, "Notebook", 2, 2500.00),
                new Produto(2L, "Mouse", 1, 75.00)
        );

        Pedido pedido = new Pedido(
                id,
                codigoCliente,
                produtos,
                LocalDateTime.now().format(DateUtils.FORMATTER_DATA_HORA_PT_BR),
                transactionId
        );
        pedido.setStatusPedido(status);
        return pedido;
    }
}