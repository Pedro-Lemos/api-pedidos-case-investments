package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.listarpedidos.response;

import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import java.util.List;
import java.util.Objects;

public class ListarPedidosResponseV1 {
    private List<Pedido> data;

    public ListarPedidosResponseV1() {
    }

    public ListarPedidosResponseV1(List<Pedido> data) {
        this.data = data;
    }

    public List<Pedido> getData() {
        return data;
    }

    public void setData(List<Pedido> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ListarPedidosResponseV1 that = (ListarPedidosResponseV1) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(data);
    }
}