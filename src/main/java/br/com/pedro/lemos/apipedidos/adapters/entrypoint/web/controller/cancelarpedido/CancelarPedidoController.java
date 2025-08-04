package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.cancelarpedido;

import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.CodigoErro;
import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.cancelarpedido.request.CancelarPedidoRequestV1;
import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.cancelarpedido.response.CancelarPedidoResponseV1;
import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.model.ErrorResponseV1;
import br.com.pedro.lemos.apipedidos.application.exception.PedidoCanceladoException;
import br.com.pedro.lemos.apipedidos.application.exception.PedidoNaoEncontradoException;
import br.com.pedro.lemos.apipedidos.application.usecase.cancelarpedidousecase.CancelarPedidoUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
public class CancelarPedidoController {

    private final CancelarPedidoUseCase cancelarPedidoUseCase;

    public CancelarPedidoController(CancelarPedidoUseCase cancelarPedidoUseCase) {
        this.cancelarPedidoUseCase = cancelarPedidoUseCase;
    }

    @PostMapping("/{pedidoId}/cancelamentos")
    public ResponseEntity<?> cancelar(@PathVariable Long pedidoId,
                                      @RequestHeader("correlationId") String correlationId,
                                      @RequestBody @Valid CancelarPedidoRequestV1 request) {
        try {
            cancelarPedidoUseCase.cancelar(pedidoId, request.getMotivoCancelamento());

            CancelarPedidoResponseV1 response = new CancelarPedidoResponseV1("Pedido cancelado com sucesso");
            return ResponseEntity.ok(response);

        } catch (PedidoNaoEncontradoException e) {
            ErrorResponseV1.DataError dataError = new ErrorResponseV1.DataError(
                    CodigoErro.PNE.getCodigo(),
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseV1(dataError));

        } catch (PedidoCanceladoException e) {
            ErrorResponseV1.DataError dataError = new ErrorResponseV1.DataError(
                    CodigoErro.PEM.getCodigo(),
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ErrorResponseV1(dataError));

        } catch (Exception e) {
            ErrorResponseV1.DataError dataError = new ErrorResponseV1.DataError(
                    CodigoErro.SER.getCodigo(),
                    "Serviço indisponível"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseV1(dataError));
        }
    }
}