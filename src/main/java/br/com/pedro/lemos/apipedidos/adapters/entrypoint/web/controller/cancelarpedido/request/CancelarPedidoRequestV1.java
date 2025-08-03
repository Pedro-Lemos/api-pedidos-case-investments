package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.cancelarpedido.request;

import jakarta.validation.constraints.NotBlank;

public class CancelarPedidoRequestV1 {

    @NotBlank(message = "Motivo do cancelamento é obrigatório")
    private String motivoCancelamento;

    public CancelarPedidoRequestV1() {
    }

    public CancelarPedidoRequestV1(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }
}