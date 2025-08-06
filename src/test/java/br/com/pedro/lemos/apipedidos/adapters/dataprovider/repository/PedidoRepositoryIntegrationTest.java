package br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository;

import br.com.pedro.lemos.apipedidos.application.exception.ProdutoNaoDisponivelException;
import br.com.pedro.lemos.apipedidos.application.service.GeradorIdPedidoService;
import br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase.EfetuarPedidoUseCase;
import br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase.model.SolicitacaoEfetuarPedido;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import br.com.pedro.lemos.apipedidos.domain.entity.Produto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(properties = {
        "app.pedidos.file.path=test-data/pedidos-test.json",
        "app.produtos.file.path=test-data/produtos-test.json"
})
@MockitoSettings(strictness = Strictness.LENIENT)
class PedidoRepositoryIntegrationTest {

    private static final String PEDIDOS_TEST_FILE = "test-data/pedidos-test.json";
    private static final String PRODUTOS_TEST_FILE = "test-data/produtos-test.json";

    @MockitoBean
    private GeradorIdPedidoService geradorIdPedidoService;

    @Autowired
    private EfetuarPedidoUseCase efetuarPedidoUseCase;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private long idPedidoCounter = 1L;

    @BeforeEach
    void setUp() throws IOException {
        // Limpa e recria os arquivos de teste antes de cada teste
        Files.deleteIfExists(Path.of(PEDIDOS_TEST_FILE));
        Files.deleteIfExists(Path.of(PRODUTOS_TEST_FILE));
        produtoRepository.init(); // Popula com dados iniciais
        when(geradorIdPedidoService.gerarId()).thenAnswer(invocation -> idPedidoCounter++);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Path.of(PEDIDOS_TEST_FILE));
        Files.deleteIfExists(Path.of(PRODUTOS_TEST_FILE));
    }

    @Test
    void deveEfetuarPedidoECriarArquivoJSON() {
        // Given
        Map<Long, Integer> produtosDesejados = Map.of(1L, 1); // 1 Notebook
        SolicitacaoEfetuarPedido solicitacao = new SolicitacaoEfetuarPedido("CLI001", produtosDesejados, "TXN001");

        // When
        efetuarPedidoUseCase.efetuar(solicitacao);

        // Then
        File arquivoJSON = new File(PEDIDOS_TEST_FILE);
        assertTrue(arquivoJSON.exists());
        assertTrue(arquivoJSON.length() > 0);
    }

    @Test
    void deveVerificarConteudoDoArquivoJSON() {
        // Given
        Map<Long, Integer> produtosDesejados = Map.of(2L, 2); // 2 Mouses
        SolicitacaoEfetuarPedido solicitacao = new SolicitacaoEfetuarPedido("CLI002", produtosDesejados, "TXN002");

        // When
        Long idPedido = efetuarPedidoUseCase.efetuar(solicitacao);
        Pedido pedidoSalvo = pedidoRepository.findByIdPedido(idPedido);

        // Then
        assertNotNull(pedidoSalvo);
        assertEquals(idPedido, pedidoSalvo.getIdPedido());
        assertEquals("CLI002", pedidoSalvo.getCodigoIdentificacaoCliente());
        assertEquals("ATIVO", pedidoSalvo.getStatusPedido());
        assertEquals(1, pedidoSalvo.getDescricaoProdutos().size());
        assertEquals(2, pedidoSalvo.getDescricaoProdutos().get(0).getQuantidadeProduto());
        assertEquals("Mouse Logitech", pedidoSalvo.getDescricaoProdutos().get(0).getNomeProduto());
    }

    @Test
    void devePermitirMultiplosPedidosComEstoqueSuficiente() {
        // Given
        // Pedido 1: 5 Notebooks (Estoque inicial: 10)
        Map<Long, Integer> produtosPedido1 = Map.of(1L, 5);
        SolicitacaoEfetuarPedido solicitacao1 = new SolicitacaoEfetuarPedido("CLI003", produtosPedido1, "TXN003");

        // Pedido 2: 3 Notebooks (Estoque restante: 5)
        Map<Long, Integer> produtosPedido2 = Map.of(1L, 3);
        SolicitacaoEfetuarPedido solicitacao2 = new SolicitacaoEfetuarPedido("CLI004", produtosPedido2, "TXN004");

        // When
        assertDoesNotThrow(() -> efetuarPedidoUseCase.efetuar(solicitacao1));
        assertDoesNotThrow(() -> efetuarPedidoUseCase.efetuar(solicitacao2));

        // Then
        Produto produto = produtoRepository.findById(1L);
        assertEquals(2, produto.getQuantidadeProduto()); // 10 - 5 - 3 = 2
    }

    @Test
    void naoDevePermitirPedidoComEstoqueInsuficiente() {
        // Given
        // Tenta comprar 11 notebooks, mas só existem 10 em estoque
        Map<Long, Integer> produtosDesejados = Map.of(1L, 11);
        SolicitacaoEfetuarPedido solicitacao = new SolicitacaoEfetuarPedido("CLI005", produtosDesejados, "TXN005");

        // When & Then
        assertThrows(ProdutoNaoDisponivelException.class, () -> {
            efetuarPedidoUseCase.efetuar(solicitacao);
        });
    }

    @Test
    void deveValidarEstruturacaoJSONCorreta() throws IOException {
        // Given
        Map<Long, Integer> produtosDesejados = Map.of(
                1L, 1, // 1 Notebook
                2L, 1  // 1 Mouse
        );
        SolicitacaoEfetuarPedido solicitacao = new SolicitacaoEfetuarPedido("CLI006", produtosDesejados, "TXN999");

        // When
        efetuarPedidoUseCase.efetuar(solicitacao);

        // Then
        File arquivoJSON = new File(PEDIDOS_TEST_FILE);
        String conteudoJSON = Files.readString(arquivoJSON.toPath());

        assertTrue(conteudoJSON.contains("\"idPedido\" : 1"));
        assertTrue(conteudoJSON.contains("\"codigoIdentificacaoCliente\" : \"CLI006\""));
        assertTrue(conteudoJSON.contains("\"statusPedido\" : \"ATIVO\""));
        assertTrue(conteudoJSON.contains("\"transactionId\" : \"TXN999\""));
        assertTrue(conteudoJSON.contains("\"descricaoProdutos\""));
        assertTrue(conteudoJSON.contains("\"Notebook Dell\""));
        assertTrue(conteudoJSON.contains("\"Mouse Logitech\""));

        assertDoesNotThrow(() -> {
            objectMapper.readValue(arquivoJSON, new TypeReference<List<Pedido>>() {});
        });
    }
}