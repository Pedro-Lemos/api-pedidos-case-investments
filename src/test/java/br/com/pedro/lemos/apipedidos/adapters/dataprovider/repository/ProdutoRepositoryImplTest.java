package br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository;

import br.com.pedro.lemos.apipedidos.domain.entity.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoRepositoryImplTest {

    @TempDir
    Path tempDir;

    private ProdutoRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        File testFile = tempDir.resolve("produtos-test.json").toFile();
        repository = new ProdutoRepositoryImpl(testFile.getAbsolutePath());
        repository.init(); // Popula com dados iniciais
    }

    @Test
    void deveSalvarEEncontrarProduto() {
        // Given
        Produto produtoNovo = new Produto(6L, "Cadeira Gamer", 5, 1200.00);

        // When
        repository.salvar(produtoNovo);
        Produto produtoEncontrado = repository.findById(6L);

        // Then
        assertNotNull(produtoEncontrado);
        assertEquals("Cadeira Gamer", produtoEncontrado.getNomeProduto());
        assertEquals(5, produtoEncontrado.getQuantidadeProduto());
    }

    @Test
    void deveAtualizarEstoqueDoProduto() {
        // Given
        Long idProduto = 1L; // Notebook Dell
        int quantidadeAReduzir = 3;
        Produto produtoOriginal = repository.findById(idProduto);
        assertNotNull(produtoOriginal);
        int estoqueInicial = produtoOriginal.getQuantidadeProduto(); // 10

        // When
        repository.atualizarEstoque(idProduto, quantidadeAReduzir);
        Produto produtoAtualizado = repository.findById(idProduto);

        // Then
        assertNotNull(produtoAtualizado);
        assertEquals(estoqueInicial - quantidadeAReduzir, produtoAtualizado.getQuantidadeProduto()); // 10 - 3 = 7
    }

    @Test
    void deveRetornarNuloSeProdutoNaoExistir() {
        // When
        Produto produto = repository.findById(999L);

        // Then
        assertNull(produto);
    }

    @Test
    void deveLancarExcecaoAoAtualizarEstoqueDeProdutoInexistente() {
        // When & Then
        assertThrows(RuntimeException.class, () -> repository.atualizarEstoque(999L, 1));
    }

    @Test
    void deveLancarExcecaoSeEstoqueFicarNegativo() {
        // Given
        Long idProduto = 2L; // Mouse Logitech, estoque 50

        // When & Then
        assertThrows(RuntimeException.class, () -> repository.atualizarEstoque(idProduto, 51));
    }

    @Test
    void deveValidarDadosIniciaisPopulados() {
        // Given
        // init() já foi chamado no setUp

        // When & Then
        Produto notebook = repository.findById(1L);
        assertNotNull(notebook);
        assertEquals("Notebook Dell", notebook.getNomeProduto());
        assertEquals(10, notebook.getQuantidadeProduto());
        assertEquals(2500.00, notebook.getPrecoUnitarioProduto());

        Produto mouse = repository.findById(2L);
        assertNotNull(mouse);
        assertEquals("Mouse Logitech", mouse.getNomeProduto());
        assertEquals(50, mouse.getQuantidadeProduto());
        assertEquals(75.00, mouse.getPrecoUnitarioProduto());

        Produto teclado = repository.findById(3L);
        assertNotNull(teclado);
        assertEquals("Teclado Mecânico", teclado.getNomeProduto());
        assertEquals(25, teclado.getQuantidadeProduto());
        assertEquals(350.00, teclado.getPrecoUnitarioProduto());

        Produto monitor = repository.findById(4L);
        assertNotNull(monitor);
        assertEquals("Monitor 24 polegadas", monitor.getNomeProduto());
        assertEquals(15, monitor.getQuantidadeProduto());
        assertEquals(800.00, monitor.getPrecoUnitarioProduto());

        Produto headset = repository.findById(5L);
        assertNotNull(headset);
        assertEquals("Headset Gamer", headset.getNomeProduto());
        assertEquals(30, headset.getQuantidadeProduto());
        assertEquals(200.00, headset.getPrecoUnitarioProduto());
    }
}