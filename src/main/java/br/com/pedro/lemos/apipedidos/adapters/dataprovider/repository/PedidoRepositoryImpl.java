package br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository;

import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
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
public class PedidoRepositoryImpl implements PedidoRepository {

    private final ObjectMapper objectMapper;
    private final String filePath;

    public PedidoRepositoryImpl(@Value("${app.pedidos.file.path:data/pedidos.json}") String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        criaArquivoSeNaoExistir();
    }

    @Override
    public Pedido findByIdPedido(Long idPedido) {
        return carregarTodosPedidos().stream()
                .filter(pedido -> pedido.getIdPedido().equals(idPedido))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void salvar(Pedido pedido) {
        try {
            List<Pedido> pedidos = carregarTodosPedidos();

            // Remove pedido existente se houver (para atualização)
            pedidos.removeIf(p -> p.getIdPedido().equals(pedido.getIdPedido()));

            // Adiciona o novo/atualizado pedido
            pedidos.add(pedido);

            // Salva a lista completa no arquivo JSON
            salvarTodosPedidos(pedidos);

        } catch (Exception e) {
            throw new RuntimeException("Não foi possível salvar o pedido", e);
        }
    }

    @Override
    public List<Pedido> findByStatus(String status) {
        List<Pedido> todosPedidos = carregarTodosPedidos();
        return todosPedidos.stream()
                .filter(pedido -> status.equals(pedido.getStatusPedido()))
                .toList();
    }

    @Override
    public List<Pedido> findAll() {
        return carregarTodosPedidos();
    }

    private List<Pedido> carregarTodosPedidos() {
        try {
            File file = new File(filePath);

            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return objectMapper.readValue(file, new TypeReference<List<Pedido>>() {});

        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void salvarTodosPedidos(List<Pedido> pedidos) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filePath), pedidos);


        } catch (IOException e) {
            throw new RuntimeException("Não foi possível salvar os pedidos no arquivo", e);
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
                salvarTodosPedidos(new ArrayList<>());
            }

        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar o arquivo da base de pedidos", e);
        }
    }
}