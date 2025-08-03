package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.efetuarpedido.request;

import br.com.pedro.lemos.apipedidos.domain.entity.Produto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public class EfetuarPedidoRequestV1 {
    @NotBlank(message = "Código de identificação do cliente é obrigatório")
    private String codigoIdentificacaoCliente;

    @NotEmpty(message = "Lista de produtos não pode estar vazia")
    @Valid
    private List<ProdutoRequest> produtos;

    public EfetuarPedidoRequestV1() {
    }

    public EfetuarPedidoRequestV1(String codigoIdentificacaoCliente, List<ProdutoRequest> produtos) {
        this.codigoIdentificacaoCliente = codigoIdentificacaoCliente;
        this.produtos = produtos;
    }

    public String getCodigoIdentificacaoCliente() {
        return codigoIdentificacaoCliente;
    }

    public void setCodigoIdentificacaoCliente(String codigoIdentificacaoCliente) {
        this.codigoIdentificacaoCliente = codigoIdentificacaoCliente;
    }

    public List<ProdutoRequest> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<ProdutoRequest> produtos) {
        this.produtos = produtos;
    }

    public static class ProdutoRequest {
        @NotNull(message = "ID do produto é obrigatório")
        private Long idProduto;

        @NotBlank(message = "Nome do produto é obrigatório")
        private String nomeProduto;

        @Min(value = 1, message = "Quantidade deve ser maior que zero")
        private int quantidadeProduto;

        @NotNull(message = "Preço unitário é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
        private Double precoUnitario;

        public ProdutoRequest() {
        }

        public ProdutoRequest(Long idProduto, String nomeProduto, int quantidadeProduto, Double precoUnitario) {
            this.idProduto = idProduto;
            this.nomeProduto = nomeProduto;
            this.quantidadeProduto = quantidadeProduto;
            this.precoUnitario = precoUnitario;
        }

        public Long getIdProduto() {
            return idProduto;
        }

        public void setIdProduto(Long idProduto) {
            this.idProduto = idProduto;
        }

        public String getNomeProduto() {
            return nomeProduto;
        }

        public void setNomeProduto(String nomeProduto) {
            this.nomeProduto = nomeProduto;
        }

        public int getQuantidadeProduto() {
            return quantidadeProduto;
        }

        public void setQuantidadeProduto(int quantidadeProduto) {
            this.quantidadeProduto = quantidadeProduto;
        }

        public Double getPrecoUnitario() {
            return precoUnitario;
        }

        public void setPrecoUnitario(Double precoUnitario) {
            this.precoUnitario = precoUnitario;
        }

        public Produto toProduto() {
            return new Produto(idProduto, nomeProduto, quantidadeProduto, precoUnitario);
        }
    }
}
