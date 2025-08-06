package br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository;

import br.com.pedro.lemos.apipedidos.domain.entity.Produto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class ProdutoRepositoryImpl implements ProdutoRepository {

    private final ObjectMapper objectMapper;
    private final String filePath;

    public ProdutoRepositoryImpl(@Value("${app.produtos.file.path:data/produtos.json}") String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        criaArquivoSeNaoExistir();
    }

    @Override
    public Produto findById(Long idProduto) {
        Map<Long, Produto> produtos = carregarTodosProdutos();
        return produtos.get(idProduto);
    }

    @Override
    public Produto salvar(Produto produto) {
        Map<Long, Produto> produtos = carregarTodosProdutos();
        produtos.put(produto.getIdProduto(), produto);
        salvarTodosProdutos(produtos);
        return produto;
    }

    @Override
    public int getEstoqueDisponivel(Long idProduto) {
        Produto produto = findById(idProduto);
        if (produto == null) {
            throw new RuntimeException("Produto com ID " + idProduto + " não encontrado.");
        }
        return produto.getQuantidadeProduto();
    }

    @Override
    public void atualizarEstoque(Long idProduto, int quantidadeComprada) {
        Map<Long, Produto> produtos = carregarTodosProdutos();
        Produto produto = produtos.get(idProduto);

        if (produto == null) {
            throw new RuntimeException("Produto com ID " + idProduto + " não encontrado para atualização de estoque.");
        }

        int novoEstoque = produto.getQuantidadeProduto() - quantidadeComprada;
        if (novoEstoque < 0) {
            throw new RuntimeException("Estoque insuficiente para o produto ID " + idProduto);
        }

        produto.setQuantidadeProduto(novoEstoque);
        salvarTodosProdutos(produtos);
    }

    private Map<Long, Produto> carregarTodosProdutos() {
        try {
            File file = new File(filePath);
            if (!file.exists() || file.length() == 0) {
                return new java.util.HashMap<>();
            }
            return objectMapper.readValue(file, new TypeReference<Map<Long, Produto>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível carregar os produtos do arquivo", e);
        }
    }

    private void salvarTodosProdutos(Map<Long, Produto> produtos) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), produtos);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível salvar os produtos no arquivo", e);
        }
    }

    private void criaArquivoSeNaoExistir() {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                Path parentDir = path.getParent();
                if (parentDir != null && !Files.exists(parentDir)) {
                    Files.createDirectories(parentDir);
                }
                // Popula com dados iniciais se o arquivo não existir
                List<Produto> produtosIniciais = List.of(
                        new Produto(1L, "Notebook Dell", 10, 2500.00),
                        new Produto(2L, "Mouse Logitech", 50, 75.00),
                        new Produto(3L, "Teclado Mecânico", 25, 350.00),
                        new Produto(4L, "Monitor 24 polegadas", 15, 800.00),
                        new Produto(5L, "Headset Gamer", 30, 200.00)
                );
                Map<Long, Produto> mapaProdutos = produtosIniciais.stream()
                        .collect(Collectors.toMap(Produto::getIdProduto, Function.identity()));
                salvarTodosProdutos(mapaProdutos);
            }
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar o arquivo da base de produtos", e);
        }
    }
}