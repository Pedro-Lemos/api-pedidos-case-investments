package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.buscarpedido;

import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.CodigoErro;
import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.buscarpedido.response.BuscarPedidoResponseV1;
import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.model.ErrorResponseV1;
import br.com.pedro.lemos.apipedidos.application.exception.PedidoNaoEncontradoException;
import br.com.pedro.lemos.apipedidos.application.usecase.buscarpedidousecase.BuscarPedidoUseCase;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
public class BuscarPedidoController {

    private final BuscarPedidoUseCase buscarPedidoUseCase;

    public BuscarPedidoController(BuscarPedidoUseCase buscarPedidoUseCase) {
        this.buscarPedidoUseCase = buscarPedidoUseCase;
    }

    @GetMapping("/{pedidoId}")
    public ResponseEntity<?> buscar(
            @PathVariable Long pedidoId,
            @RequestHeader("correlationId") String correlationId
    ) {
        try {
            Pedido pedido = buscarPedidoUseCase.buscar(pedidoId);

            BuscarPedidoResponseV1 response = new BuscarPedidoResponseV1(pedido);
            return ResponseEntity.ok(response);
        } catch (PedidoNaoEncontradoException e) {
            ErrorResponseV1.DataError dataError = new ErrorResponseV1.DataError(
                    CodigoErro.PNE.getCodigo(),
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
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
