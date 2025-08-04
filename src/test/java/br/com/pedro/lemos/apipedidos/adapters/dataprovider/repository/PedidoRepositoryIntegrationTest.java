package br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository;

import br.com.pedro.lemos.apipedidos.application.service.GeradorIdPedidoService;
import br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase.EfetuarPedidoUseCase;
import br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase.model.SolicitacaoEfetuarPedido;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import br.com.pedro.lemos.apipedidos.domain.entity.Produto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {
        "app.pedidos.file.path=test-data/pedidos-test.json",
        "app.produtos.file.path=test-data/produtos-test.json"
})
@MockitoSettings(strictness = Strictness.LENIENT)
class PedidoRepositoryIntegrationTest {

    @MockitoBean
    private GeradorIdPedidoService geradorIdPedidoService;

    @Autowired
    private EfetuarPedidoUseCase efetuarPedidoUseCase;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    private ObjectMapper objectMapper;
    private Long contadorId = 1L;

    @BeforeEach
    void setUp() throws IOException {
        objectMapper = new ObjectMapper();
        contadorId = 1L;

        // Reset do mock
        reset(geradorIdPedidoService);

        // Mock do gerador para retornar IDs sequenciais
        when(geradorIdPedidoService.gerarId()).thenAnswer(invocation -> contadorId++);

        // Limpa os arquivos de teste
        Path testFilePedidos = Path.of("test-data/pedidos-test.json");
        Path testFileProdutos = Path.of("test-data/produtos-test.json");

        if (Files.exists(testFilePedidos)) {
            Files.delete(testFilePedidos);
        }

        if (Files.exists(testFileProdutos)) {
            Files.delete(testFileProdutos);
        }

        // Força a recriação dos arquivos
        if (pedidoRepository instanceof PedidoRepositoryImpl) {
            ((PedidoRepositoryImpl) pedidoRepository).init();
        }

        if (produtoRepository instanceof ProdutoRepositoryImpl) {
            ((ProdutoRepositoryImpl) produtoRepository).init();
        }
    }

    @Test
    void deveEfetuarPedidoECriarArquivoJSON() {
        String codigoCliente = "CLI001";
        String transactionId = "TXN123";

        assertNotNull(produtoRepository.findById(1L), "Produto 1 deve existir");
        assertNotNull(produtoRepository.findById(2L), "Produto 2 deve existir");

        List<Produto> produtos = Arrays.asList(
                new Produto(1L, "Notebook Dell", 2, 2500.00),
                new Produto(2L, "Mouse Logitech", 5, 75.00)
        );

        SolicitacaoEfetuarPedido solicitacao = new SolicitacaoEfetuarPedido(
                codigoCliente, produtos, transactionId
        );

        Long idGerado = efetuarPedidoUseCase.efetuar(solicitacao);

        assertEquals(1L, idGerado);

        Pedido pedidoSalvo = pedidoRepository.findByIdPedido(1L);

        assertNotNull(pedidoSalvo);
        assertEquals(1L, pedidoSalvo.getIdPedido());
        assertEquals(codigoCliente, pedidoSalvo.getCodigoIdentificacaoCliente());
        assertEquals("ATIVO", pedidoSalvo.getStatusPedido());
        assertEquals(transactionId, pedidoSalvo.getTransactionId());
        assertEquals(2, pedidoSalvo.getDescricaoProdutos().size());

        Path arquivoJSON = Path.of("test-data/pedidos-test.json");
        assertTrue(Files.exists(arquivoJSON));

        assertEquals(8, produtoRepository.getEstoqueDisponivel(1L));
        assertEquals(45, produtoRepository.getEstoqueDisponivel(2L));
    }

    @Test
    void deveVerificarConteudoDoArquivoJSON() throws IOException {
        String codigoCliente = "CLI002";
        String transactionId = "TXN456";

        List<Produto> produtos = Arrays.asList(
                new Produto(3L, "Teclado Mecânico", 3, 350.00)
        );

        SolicitacaoEfetuarPedido solicitacao = new SolicitacaoEfetuarPedido(
                codigoCliente, produtos, transactionId
        );

        Long idGerado = efetuarPedidoUseCase.efetuar(solicitacao);
        assertEquals(1L, idGerado);

        Path arquivoJSON = Path.of("test-data/pedidos-test.json");
        assertTrue(Files.exists(arquivoJSON));

        List<Pedido> pedidosNoArquivo = objectMapper.readValue(
                new File("test-data/pedidos-test.json"),
                new TypeReference<List<Pedido>>() {}
        );

        assertEquals(1, pedidosNoArquivo.size());

        Pedido pedidoNoArquivo = pedidosNoArquivo.get(0);
        assertEquals(1L, pedidoNoArquivo.getIdPedido());
        assertEquals(codigoCliente, pedidoNoArquivo.getCodigoIdentificacaoCliente());
        assertEquals("ATIVO", pedidoNoArquivo.getStatusPedido());
        assertEquals(transactionId, pedidoNoArquivo.getTransactionId());
        assertEquals(1, pedidoNoArquivo.getDescricaoProdutos().size());

        Produto produtoNoArquivo = pedidoNoArquivo.getDescricaoProdutos().get(0);
        assertEquals(3L, produtoNoArquivo.getIdProduto());
        assertEquals("Teclado Mecânico", produtoNoArquivo.getNomeProduto());
        assertEquals(3, produtoNoArquivo.getQuantidadeProduto());
        assertEquals(350.00, produtoNoArquivo.getPrecoUnitarioProduto());

        assertEquals(22, produtoRepository.getEstoqueDisponivel(3L));
    }

    @Test
    void devePermitirMultiplosPedidosComEstoqueSuficiente() {
        List<Produto> produtos1 = Arrays.asList(
                new Produto(5L, "Headset Gamer", 5, 200.00)
        );

        List<Produto> produtos2 = Arrays.asList(
                new Produto(5L, "Headset Gamer", 10, 200.00)
        );

        SolicitacaoEfetuarPedido solicitacao1 = new SolicitacaoEfetuarPedido(
                "CLI004", produtos1, "TXN001"
        );

        SolicitacaoEfetuarPedido solicitacao2 = new SolicitacaoEfetuarPedido(
                "CLI005", produtos2, "TXN002"
        );

        Long id1 = efetuarPedidoUseCase.efetuar(solicitacao1);
        Long id2 = efetuarPedidoUseCase.efetuar(solicitacao2);

        assertEquals(1L, id1);
        assertEquals(2L, id2);

        Pedido pedido1 = pedidoRepository.findByIdPedido(1L);
        Pedido pedido2 = pedidoRepository.findByIdPedido(2L);

        assertNotNull(pedido1);
        assertNotNull(pedido2);

        assertEquals(15, produtoRepository.getEstoqueDisponivel(5L));
    }

    @Test
    void deveValidarEstruturacaoJSONCorreta() throws IOException {
        String codigoCliente = "CLI006";
        String transactionId = "TXN999";

        List<Produto> produtos = Arrays.asList(
                new Produto(1L, "Notebook Dell", 1, 2500.00),
                new Produto(2L, "Mouse Logitech", 2, 75.00)
        );

        SolicitacaoEfetuarPedido solicitacao = new SolicitacaoEfetuarPedido(
                codigoCliente, produtos, transactionId
        );

        Long idGerado = efetuarPedidoUseCase.efetuar(solicitacao);
        assertEquals(1L, idGerado);

        Path arquivoJSON = Path.of("test-data/pedidos-test.json");
        String conteudoJSON = Files.readString(arquivoJSON);

        assertTrue(conteudoJSON.contains("\"idPedido\" : 1"));
        assertTrue(conteudoJSON.contains("\"codigoIdentificacaoCliente\" : \"CLI006\""));
        assertTrue(conteudoJSON.contains("\"statusPedido\" : \"ATIVO\""));
        assertTrue(conteudoJSON.contains("\"transactionId\" : \"TXN999\""));
        assertTrue(conteudoJSON.contains("\"descricaoProdutos\""));
        assertTrue(conteudoJSON.contains("\"Notebook Dell\""));
        assertTrue(conteudoJSON.contains("\"Mouse Logitech\""));

        assertDoesNotThrow(() -> {
            objectMapper.readValue(new File("test-data/pedidos-test.json"),
                    new TypeReference<List<Pedido>>() {});
        });
    }
}