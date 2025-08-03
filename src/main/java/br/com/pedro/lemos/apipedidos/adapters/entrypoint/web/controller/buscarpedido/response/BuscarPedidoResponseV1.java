package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.buscarpedido.response;

import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;

import java.util.Objects;

public class BuscarPedidoResponseV1 {
    private Pedido data;

    public BuscarPedidoResponseV1() {

    }

    public BuscarPedidoResponseV1(Pedido data) {
        this.data = data;
    }

    public Pedido getData() {
        return data;
    }

    public void setData(Pedido data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BuscarPedidoResponseV1 that = (BuscarPedidoResponseV1) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(data);
    }
}
