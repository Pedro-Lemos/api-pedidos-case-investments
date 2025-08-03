package br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository;

import br.com.pedro.lemos.apipedidos.domain.entity.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoRepositoryImplTest {

    @TempDir
    Path tempDir;

    private ProdutoRepositoryImpl repository;
    private String filePath;

    @BeforeEach
    void setUp() {
        filePath = tempDir.resolve("produtos-test.json").toString();
        repository = new ProdutoRepositoryImpl(filePath);
    }

    @Test
    void deveCriarArquivoEPopularDadosIniciais() {
        
        repository.init();

        
        File file = new File(filePath);
        assertTrue(file.exists());

        // Verifica se os dados iniciais foram criados
        assertNotNull(repository.findById(1L));
        assertNotNull(repository.findById(2L));
        assertNotNull(repository.findById(3L));
        assertNotNull(repository.findById(4L));
        assertNotNull(repository.findById(5L));
    }

    @Test
    void deveEncontrarProdutoPorId() {
        
        repository.init();

        
        Produto notebook = repository.findById(1L);
        Produto mouse = repository.findById(2L);

        
        assertNotNull(notebook);
        assertEquals("Notebook Dell", notebook.getNomeProduto());
        assertEquals(10, notebook.getQuantidadeProduto());
        assertEquals(2500.00, notebook.getPrecoUnitarioProduto());

        assertNotNull(mouse);
        assertEquals("Mouse Logitech", mouse.getNomeProduto());
        assertEquals(50, mouse.getQuantidadeProduto());
        assertEquals(75.00, mouse.getPrecoUnitarioProduto());
    }

    @Test
    void deveRetornarNullQuandoProdutoNaoExistir() {
        
        repository.init();

        
        Produto produto = repository.findById(999L);

        
        assertNull(produto);
    }

    @Test
    void deveSalvarNovoProduto() {
        
        repository.init();
        Produto novoProduto = new Produto(10L, "Webcam HD", 15, 299.99);

        
        Produto produtoSalvo = repository.salvar(novoProduto);

        
        assertNotNull(produtoSalvo);
        assertEquals(10L, produtoSalvo.getIdProduto());

        Produto produtoEncontrado = repository.findById(10L);
        assertNotNull(produtoEncontrado);
        assertEquals("Webcam HD", produtoEncontrado.getNomeProduto());
        assertEquals(15, produtoEncontrado.getQuantidadeProduto());
        assertEquals(299.99, produtoEncontrado.getPrecoUnitarioProduto());
    }

    @Test
    void deveAtualizarProdutoExistente() {
        
        repository.init();
        Produto produtoOriginal = repository.findById(1L);
        produtoOriginal.setQuantidadeProduto(20);
        produtoOriginal.setPrecoUnitarioProduto(2800.00);

        
        repository.salvar(produtoOriginal);

        
        Produto produtoAtualizado = repository.findById(1L);
        assertEquals(20, produtoAtualizado.getQuantidadeProduto());
        assertEquals(2800.00, produtoAtualizado.getPrecoUnitarioProduto());
        assertEquals("Notebook Dell", produtoAtualizado.getNomeProduto()); // nome não mudou
    }

    @Test
    void deveRetornarEstoqueDisponivel() {
        
        repository.init();

        
        int estoqueNotebook = repository.getEstoqueDisponivel(1L);
        int estoqueMouse = repository.getEstoqueDisponivel(2L);
        int estoqueProdutoInexistente = repository.getEstoqueDisponivel(999L);

        
        assertEquals(10, estoqueNotebook);
        assertEquals(50, estoqueMouse);
        assertEquals(0, estoqueProdutoInexistente);
    }

    @Test
    void deveAtualizarEstoqueCorretamente() {
        repository.init();
        int estoqueInicial = repository.getEstoqueDisponivel(1L);

        repository.atualizarEstoque(1L, 3);

        
        int estoqueAtualizado = repository.getEstoqueDisponivel(1L);
        assertEquals(estoqueInicial - 3, estoqueAtualizado);
        assertEquals(7, estoqueAtualizado); // 10 - 3 = 7
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueFicarNegativo() {
        repository.init();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            repository.atualizarEstoque(1L, 15); // tenta tirar 15 de 10 disponíveis
        });

        assertTrue(exception.getMessage().contains("Estoque não pode ser negativo"));
        assertTrue(exception.getMessage().contains("produto: 1"));
    }

    @Test
    void deveLancarExcecaoQuandoTentarAtualizarEstoqueDeProdutoInexistente() {
        repository.init();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            repository.atualizarEstoque(999L, 5);
        });

        assertTrue(exception.getMessage().contains("Produto não encontrado: 999"));
    }

    @Test
    void devePermitirMultiplasAtualizacoesDeEstoque() {
        repository.init();
        int estoqueInicial = repository.getEstoqueDisponivel(2L); // Mouse: 50

        repository.atualizarEstoque(2L, 10); // 50 - 10 = 40
        repository.atualizarEstoque(2L, 5);  // 40 - 5 = 35
        repository.atualizarEstoque(2L, 15); // 35 - 15 = 20

        assertEquals(20, repository.getEstoqueDisponivel(2L));
    }

    @Test
    void deveManterDadosAposRecriarRepository() {
        repository.init();
        Produto novoProduto = new Produto(20L, "SSD 1TB", 8, 599.99);
        repository.salvar(novoProduto);

        ProdutoRepositoryImpl novoRepository = new ProdutoRepositoryImpl(filePath);

        Produto produtoCarregado = novoRepository.findById(20L);
        assertNotNull(produtoCarregado);
        assertEquals("SSD 1TB", produtoCarregado.getNomeProduto());
        assertEquals(8, produtoCarregado.getQuantidadeProduto());
        assertEquals(599.99, produtoCarregado.getPrecoUnitarioProduto());
    }

    @Test
    void naoDevePopularDadosQuandoArquivoJaTemConteudo() throws IOException {
        Produto produtoCustomizado = new Produto(100L, "Produto Custom", 5, 123.45);
        repository.init();
        repository.salvar(produtoCustomizado);

        ProdutoRepositoryImpl novoRepository = new ProdutoRepositoryImpl(filePath);
        novoRepository.init();

        assertNotNull(novoRepository.findById(100L));
        assertEquals("Produto Custom", novoRepository.findById(100L).getNomeProduto());
    }

    @Test
    void deveCarregarListaVaziaQuandoArquivoTemJSONInvalido() throws IOException {
        Files.write(Path.of(filePath), "json inválido".getBytes());

        Produto produto = repository.findById(1L);

        assertNull(produto);
    }

    @Test
    void deveValidarDadosIniciaisPopulados() {
        repository.init();

        Produto notebook = repository.findById(1L);
        assertEquals("Notebook Dell", notebook.getNomeProduto());
        assertEquals(10, notebook.getQuantidadeProduto());
        assertEquals(2500.00, notebook.getPrecoUnitarioProduto());

        Produto mouse = repository.findById(2L);
        assertEquals("Mouse Logitech", mouse.getNomeProduto());
        assertEquals(50, mouse.getQuantidadeProduto());
        assertEquals(75.00, mouse.getPrecoUnitarioProduto());

        Produto teclado = repository.findById(3L);
        assertEquals("Teclado Mecânico", teclado.getNomeProduto());
        assertEquals(25, teclado.getQuantidadeProduto());
        assertEquals(350.00, teclado.getPrecoUnitarioProduto());

        Produto monitor = repository.findById(4L);
        assertEquals("Monitor 24 polegadas", monitor.getNomeProduto());
        assertEquals(15, monitor.getQuantidadeProduto());
        assertEquals(800.00, monitor.getPrecoUnitarioProduto());

        Produto headset = repository.findById(5L);
        assertEquals("Headset Gamer", headset.getNomeProduto());
        assertEquals(30, headset.getQuantidadeProduto());
        assertEquals(200.00, headset.getPrecoUnitarioProduto());
    }
}