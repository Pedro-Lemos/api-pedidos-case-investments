package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.cancelarpedido.response;

import java.util.Objects;

public class CancelarPedidoResponseV1 {
    private DataResponse data;

    public CancelarPedidoResponseV1() {
    }

    public CancelarPedidoResponseV1(String mensagem) {
        this.data = new DataResponse(mensagem);
    }

    public DataResponse getData() {
        return data;
    }

    public void setData(DataResponse data) {
        this.data = data;
    }

    public static class DataResponse {
        private String mensagem;

        public DataResponse() {
        }

        public DataResponse(String mensagem) {
            this.mensagem = mensagem;
        }

        public String getMensagem() {
            return mensagem;
        }

        public void setMensagem(String mensagem) {
            this.mensagem = mensagem;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            DataResponse that = (DataResponse) o;
            return Objects.equals(mensagem, that.mensagem);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(mensagem);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CancelarPedidoResponseV1 that = (CancelarPedidoResponseV1) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(data);
    }
}