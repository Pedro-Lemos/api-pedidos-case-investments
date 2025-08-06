package br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository;

import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        return carregarTodosPedidos().get(idPedido);
    }

    @Override
    public void salvar(Pedido pedido) {
        try {
            Map<Long, Pedido> pedidos = carregarTodosPedidos();
            pedidos.put(pedido.getIdPedido(), pedido);
            salvarTodosPedidos(pedidos);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível salvar o pedido", e);
        }
    }

    @Override
    public Page<Pedido> findByStatus(String status, Pageable pageable) {
        List<Pedido> pedidosFiltrados = carregarTodosPedidos().values().stream()
                .filter(pedido -> status.equals(pedido.getStatusPedido()))
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), pedidosFiltrados.size());

        List<Pedido> pageContent = (start > end) ? Collections.emptyList() : new ArrayList<>(pedidosFiltrados.subList(start, end));

        return new PageImpl<>(pageContent, pageable, pedidosFiltrados.size());
    }

    @Override
    public List<Pedido> findAll() {
        return new ArrayList<>(carregarTodosPedidos().values());
    }

    private Map<Long, Pedido> carregarTodosPedidos() {
        try {
            File file = new File(filePath);
            if (!file.exists() || file.length() == 0) {
                return new java.util.HashMap<>();
            }
            return objectMapper.readValue(file, new TypeReference<Map<Long, Pedido>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível carregar os pedidos do arquivo", e);
        }
    }

    private void salvarTodosPedidos(Map<Long, Pedido> pedidos) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), pedidos);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível salvar os pedidos no arquivo", e);
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
                salvarTodosPedidos(new java.util.HashMap<>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar o arquivo da base de pedidos", e);
        }
    }
}