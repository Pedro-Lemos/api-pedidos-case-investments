package br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository;

import br.com.pedro.lemos.apipedidos.domain.entity.Produto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProdutoRepositoryImpl implements ProdutoRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProdutoRepositoryImpl.class);

    private final ObjectMapper objectMapper;
    private final String filePath;

    public ProdutoRepositoryImpl(@Value("${app.produtos.file.path}") String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        criaArquivoSeNaoExistir();
        popularDadosIniciais();
    }

    @Override
    public Produto findById(Long idProduto) {
        return carregarTodosProdutos().stream()
                .filter(produto -> produto.getIdProduto().equals(idProduto))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Produto salvar(Produto produto) {
        try {
            List<Produto> produtos = carregarTodosProdutos();

            // Remove produto existente se houver (para atualização)
            produtos.removeIf(p -> p.getIdProduto().equals(produto.getIdProduto()));

            // Adiciona o novo/atualizado produto
            produtos.add(produto);

            // Salva a lista completa no arquivo JSON
            salvarTodosProdutos(produtos);


            return produto;

        } catch (Exception e) {
            throw new RuntimeException("Não foi possível salvar o produto", e);
        }
    }

    @Override
    public int getEstoqueDisponivel(Long idProduto) {
        Produto produto = findById(idProduto);
        return produto != null ? produto.getQuantidadeProduto() : 0;
    }

    @Override
    public void atualizarEstoque(Long idProduto, int quantidadeAtualizada) {
        List<Produto> produtos = carregarTodosProdutos();

        produtos.stream()
                .filter(p -> p.getIdProduto().equals(idProduto))
                .findFirst()
                .ifPresentOrElse(produto -> {
                    int estoqueAtual = produto.getQuantidadeProduto();
                    int novoEstoque = estoqueAtual - quantidadeAtualizada;

                    if (novoEstoque < 0) {
                        throw new RuntimeException("Estoque não pode ser negativo para produto: " + idProduto);
                    }

                    produto.setQuantidadeProduto(novoEstoque);
                    salvarTodosProdutos(produtos);


                }, () -> {
                    throw new RuntimeException("Produto não encontrado: " + idProduto);
                });
    }

    private List<Produto> carregarTodosProdutos() {
        try {
            File file = new File(filePath);

            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            List<Produto> produtos = objectMapper.readValue(file, new TypeReference<List<Produto>>() {});

            return produtos;

        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void salvarTodosProdutos(List<Produto> produtos) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filePath), produtos);


        } catch (IOException e) {
            throw new RuntimeException("Não foi possível salvar os produtos no arquivo", e);
        }
    }

    private void criaArquivoSeNaoExistir() {
        try {
            Path path = Paths.get(filePath);
            Path parentDir = path.getParent();

            // Cria o diretório pai se não existir
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            // Cria o arquivo JSON vazio se não existir
            if (!Files.exists(path)) {
                salvarTodosProdutos(new ArrayList<>());
            }

        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar o arquivo da base de produtos", e);
        }
    }

    private void popularDadosIniciais() {
        List<Produto> produtos = carregarTodosProdutos();

        if (produtos.isEmpty()) {
            List<Produto> produtosIniciais = List.of(
                    new Produto(1L, "Notebook Dell", 10, 2500.00),
                    new Produto(2L, "Mouse Logitech", 50, 75.00),
                    new Produto(3L, "Teclado Mecânico", 25, 350.00),
                    new Produto(4L, "Monitor 24 polegadas", 15, 800.00),
                    new Produto(5L, "Headset Gamer", 30, 200.00)
            );

            salvarTodosProdutos(produtosIniciais);
            logger.info("Dados iniciais de produtos criados");
        }
    }
}