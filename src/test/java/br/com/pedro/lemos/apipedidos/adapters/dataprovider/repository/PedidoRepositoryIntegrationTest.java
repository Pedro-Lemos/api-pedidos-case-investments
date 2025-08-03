package br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository;

import br.com.pedro.lemos.apipedidos.application.exception.ProdutoNaoDisponivelException;
import br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase.EfetuarPedidoUseCase;
import br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase.model.SolicitacaoEfetuarPedido;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import br.com.pedro.lemos.apipedidos.domain.entity.Produto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "app.pedidos.file.path=test-data/pedidos-test.json",
        "app.produtos.file.path=test-data/produtos-test.json"
})
class PedidoRepositoryIntegrationTest {

    @Autowired
    private EfetuarPedidoUseCase efetuarPedidoUseCase;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        objectMapper = new ObjectMapper();

        // Limpa os arquivos de teste para começar cada teste do zero
        Path testFilePedidos = Path.of("test-data/pedidos-test.json");
        Path testFileProdutos = Path.of("test-data/produtos-test.json");

        if (Files.exists(testFilePedidos)) {
            Files.delete(testFilePedidos);
        }

        if (Files.exists(testFileProdutos)) {
            Files.delete(testFileProdutos);
        }

        // Força a recriação dos arquivos com dados iniciais
        if (pedidoRepository instanceof PedidoRepositoryImpl) {
            ((PedidoRepositoryImpl) pedidoRepository).init();
        }

        if (produtoRepository instanceof ProdutoRepositoryImpl) {
            produtoRepository.init();
        }
    }

    @Test
    void deveEfetuarPedidoECriarArquivoJSON() {
        
        Long idPedido = 1L;
        String codigoCliente = "CLI001";
        String transactionId = "TXN123";

        assertNotNull(produtoRepository.findById(1L), "Produto 1 deve existir");
        assertNotNull(produtoRepository.findById(2L), "Produto 2 deve existir");

        List<Produto> produtos = Arrays.asList(
                new Produto(1L, "Notebook Dell", 2, 2500.00), // Pede 2 de 10 disponíveis
                new Produto(2L, "Mouse Logitech", 5, 75.00)   // Pede 5 de 50 disponíveis
        );

        SolicitacaoEfetuarPedido solicitacao = new SolicitacaoEfetuarPedido(
                idPedido, codigoCliente, produtos, transactionId
        );

        
        efetuarPedidoUseCase.efetuar(solicitacao);

        
        Pedido pedidoSalvo = pedidoRepository.findByIdPedido(idPedido);

        assertNotNull(pedidoSalvo);
        assertEquals(idPedido, pedidoSalvo.getIdPedido());
        assertEquals(codigoCliente, pedidoSalvo.getCodigoIdentificacaoCliente());
        assertEquals("ATIVO", pedidoSalvo.getStatusPedido());
        assertEquals(transactionId, pedidoSalvo.getTransactionId());
        assertEquals(2, pedidoSalvo.getDescricaoProdutos().size());

        // Verifica se o arquivo JSON foi criado
        Path arquivoJSON = Path.of("test-data/pedidos-test.json");
        assertTrue(Files.exists(arquivoJSON));

        // Verifica se o estoque foi atualizado corretamente
        assertEquals(8, produtoRepository.getEstoqueDisponivel(1L)); // 10 - 2 = 8
        assertEquals(45, produtoRepository.getEstoqueDisponivel(2L)); // 50 - 5 = 45
    }

    @Test
    void deveLancarExcecaoQuandoSegundoProdutoTemEstoqueInsuficiente() {
        Long idPedido = 7L;
        String codigoCliente = "CLI007";
        String transactionId = "TXN888";

        List<Produto> produtos = Arrays.asList(
                new Produto(1L, "Notebook Dell", 2, 2500.00),     // OK: pede 2 de 10 disponíveis
                new Produto(2L, "Mouse Logitech", 60, 75.00)      // ERRO: pede 60 de 50 disponíveis
        );

        SolicitacaoEfetuarPedido solicitacao = new SolicitacaoEfetuarPedido(
                idPedido, codigoCliente, produtos, transactionId
        );

        ProdutoNaoDisponivelException exception = assertThrows(
                ProdutoNaoDisponivelException.class,
                () -> efetuarPedidoUseCase.efetuar(solicitacao)
        );

        // Verifica que a exceção contém o nome do produto problemático
        assertTrue(exception.getMessage().contains("Mouse Logitech"));

        // Verifica que NENHUM pedido foi salvo
        Pedido pedidoNaoSalvo = pedidoRepository.findByIdPedido(idPedido);
        assertNull(pedidoNaoSalvo);

        // Verifica que NENHUM estoque foi alterado (rollback implícito)
        assertEquals(10, produtoRepository.getEstoqueDisponivel(1L)); // Notebook não foi alterado
        assertEquals(50, produtoRepository.getEstoqueDisponivel(2L)); // Mouse não foi alterado
    }

    @Test
    void deveVerificarConteudoDoArquivoJSON() throws IOException {
        Long idPedido = 2L;
        String codigoCliente = "CLI002";
        String transactionId = "TXN456";

        List<Produto> produtos = Arrays.asList(
                new Produto(3L, "Teclado Mecânico", 3, 350.00) // Pede 3 de 25 disponíveis
        );

        SolicitacaoEfetuarPedido solicitacao = new SolicitacaoEfetuarPedido(
                idPedido, codigoCliente, produtos, transactionId
        );

        
        efetuarPedidoUseCase.efetuar(solicitacao);

        
        Path arquivoJSON = Path.of("test-data/pedidos-test.json");
        assertTrue(Files.exists(arquivoJSON));

        // Verifica o conteúdo do JSON
        List<Pedido> pedidosNoArquivo = objectMapper.readValue(
                new File("test-data/pedidos-test.json"),
                new TypeReference<List<Pedido>>() {}
        );

        assertEquals(1, pedidosNoArquivo.size());

        Pedido pedidoNoArquivo = pedidosNoArquivo.get(0);
        assertEquals(idPedido, pedidoNoArquivo.getIdPedido());
        assertEquals(codigoCliente, pedidoNoArquivo.getCodigoIdentificacaoCliente());
        assertEquals("ATIVO", pedidoNoArquivo.getStatusPedido());
        assertEquals(transactionId, pedidoNoArquivo.getTransactionId());
        assertEquals(1, pedidoNoArquivo.getDescricaoProdutos().size());

        Produto produtoNoArquivo = pedidoNoArquivo.getDescricaoProdutos().get(0);
        assertEquals(3L, produtoNoArquivo.getIdProduto());
        assertEquals("Teclado Mecânico", produtoNoArquivo.getNomeProduto());
        assertEquals(3, produtoNoArquivo.getQuantidadeProduto());
        assertEquals(350.00, produtoNoArquivo.getPrecoUnitarioProduto());

        // Verifica se o estoque foi atualizado
        assertEquals(22, produtoRepository.getEstoqueDisponivel(3L)); // 25 - 3 = 22
    }

    @Test
    void deveValidarEstoqueAntesDeEfetuarPedido() {
        Long idPedido = 3L;
        String codigoCliente = "CLI003";
        String transactionId = "TXN789";

        List<Produto> produtos = Arrays.asList(
                new Produto(4L, "Monitor 24 polegadas", 20, 800.00) // Tenta pedir 20 de 15 disponíveis
        );

        SolicitacaoEfetuarPedido solicitacao = new SolicitacaoEfetuarPedido(
                idPedido, codigoCliente, produtos, transactionId
        );

        assertThrows(RuntimeException.class, () -> {
            efetuarPedidoUseCase.efetuar(solicitacao);
        });

        // Verifica que o pedido não foi salvo
        Pedido pedidoNaoSalvo = pedidoRepository.findByIdPedido(idPedido);
        assertNull(pedidoNaoSalvo);

        // Verifica que o estoque não foi alterado
        assertEquals(15, produtoRepository.getEstoqueDisponivel(4L));
    }

    @Test
    void devePermitirMultiplosPedidosComEstoqueSuficiente() {
        List<Produto> produtos1 = Arrays.asList(
                new Produto(5L, "Headset Gamer", 5, 200.00) // Primeiro pedido: 5 de 30
        );

        List<Produto> produtos2 = Arrays.asList(
                new Produto(5L, "Headset Gamer", 10, 200.00) // Segundo pedido: 10 do restante
        );

        SolicitacaoEfetuarPedido solicitacao1 = new SolicitacaoEfetuarPedido(
                4L, "CLI004", produtos1, "TXN001"
        );

        SolicitacaoEfetuarPedido solicitacao2 = new SolicitacaoEfetuarPedido(
                5L, "CLI005", produtos2, "TXN002"
        );

        
        efetuarPedidoUseCase.efetuar(solicitacao1);
        efetuarPedidoUseCase.efetuar(solicitacao2);

        
        Pedido pedido1 = pedidoRepository.findByIdPedido(4L);
        Pedido pedido2 = pedidoRepository.findByIdPedido(5L);

        assertNotNull(pedido1);
        assertNotNull(pedido2);

        // Verifica estoque final: 30 - 5 - 10 = 15
        assertEquals(15, produtoRepository.getEstoqueDisponivel(5L));
    }

    @Test
    void deveValidarEstruturacaoJSONCorreta() throws IOException {
        
        Long idPedido = 6L;
        String codigoCliente = "CLI006";
        String transactionId = "TXN999";

        List<Produto> produtos = Arrays.asList(
                new Produto(1L, "Notebook Dell", 1, 2500.00),
                new Produto(2L, "Mouse Logitech", 2, 75.00)
        );

        SolicitacaoEfetuarPedido solicitacao = new SolicitacaoEfetuarPedido(
                idPedido, codigoCliente, produtos, transactionId
        );

        
        efetuarPedidoUseCase.efetuar(solicitacao);

        Path arquivoJSON = Path.of("test-data/pedidos-test.json");
        String conteudoJSON = Files.readString(arquivoJSON);

        assertTrue(conteudoJSON.contains("\"idPedido\" : 6"));
        assertTrue(conteudoJSON.contains("\"codigoIdentificacaoCliente\" : \"CLI006\""));
        assertTrue(conteudoJSON.contains("\"statusPedido\" : \"ATIVO\""));
        assertTrue(conteudoJSON.contains("\"transactionId\" : \"TXN999\""));
        assertTrue(conteudoJSON.contains("\"descricaoProdutos\""));
        assertTrue(conteudoJSON.contains("\"Notebook Dell\""));
        assertTrue(conteudoJSON.contains("\"Mouse Logitech\""));

        // Verifica que é um JSON válido parseando novamente
        assertDoesNotThrow(() -> {
            objectMapper.readValue(new File("test-data/pedidos-test.json"),
                    new TypeReference<List<Pedido>>() {});
        });
    }
}