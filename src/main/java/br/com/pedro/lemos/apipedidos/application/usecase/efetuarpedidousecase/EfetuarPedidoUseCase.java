package br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase;

import br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase.model.SolicitacaoEfetuarPedido;

public interface EfetuarPedidoUseCase {
    void efetuar(SolicitacaoEfetuarPedido solicitacaoEfetuarPedido);
}
