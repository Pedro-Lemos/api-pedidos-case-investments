package br.com.pedro.lemos.apipedidos.adapters.dataprovider.repository;

import br.com.pedro.lemos.apipedidos.domain.entity.Produto;

public interface ProdutoRepository {
    Produto findById(Long idProduto);
    Produto salvar(Produto produto);
    int getEstoqueDisponivel(Long idProduto);
    void atualizarEstoque(Long idProduto, int quantidadeAtualizada);
    void init();
}
