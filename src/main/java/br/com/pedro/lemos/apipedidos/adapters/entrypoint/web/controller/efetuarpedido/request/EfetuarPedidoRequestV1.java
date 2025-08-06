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

        @Min(value = 1, message = "Quantidade deve ser maior que zero")
        private int quantidadeProduto;

        public ProdutoRequest() {
        }

        public ProdutoRequest(Long idProduto, int quantidadeProduto) {
            this.idProduto = idProduto;
            this.quantidadeProduto = quantidadeProduto;
        }

        public Long getIdProduto() {
            return idProduto;
        }

        public void setIdProduto(Long idProduto) {
            this.idProduto = idProduto;
        }

        public int getQuantidadeProduto() {
            return quantidadeProduto;
        }

        public void setQuantidadeProduto(int quantidadeProduto) {
            this.quantidadeProduto = quantidadeProduto;
        }

        public Produto toProduto() {
            return new Produto(idProduto, quantidadeProduto);
        }
    }
}
