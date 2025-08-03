package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.efetuarpedido.response;

public class EfetuarPedidoResponseV1 {
    private DataResponse data;

    public EfetuarPedidoResponseV1() {
    }

    public EfetuarPedidoResponseV1(DataResponse data) {
        this.data = data;
    }

    public DataResponse getData() {
        return data;
    }

    public void setData(DataResponse data) {
        this.data = data;
    }

    public static class DataResponse {
        private String mensagem;
        private Long idPedido;

        public DataResponse() {
        }

        public DataResponse(String mensagem, Long idPedido) {
            this.mensagem = mensagem;
            this.idPedido = idPedido;
        }

        public String getMensagem() {
            return mensagem;
        }

        public void setMensagem(String mensagem) {
            this.mensagem = mensagem;
        }

        public Long getIdPedido() {
            return idPedido;
        }

        public void setIdPedido(Long idPedido) {
            this.idPedido = idPedido;
        }
    }

}
