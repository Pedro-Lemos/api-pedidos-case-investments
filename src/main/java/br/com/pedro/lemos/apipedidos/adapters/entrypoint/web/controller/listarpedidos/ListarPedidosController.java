package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.listarpedidos;

import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.CodigoErro;
import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.listarpedidos.response.ListarPedidosResponseV1;
import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.model.ErrorResponseV1;
import br.com.pedro.lemos.apipedidos.application.exception.PedidosInativosException;
import br.com.pedro.lemos.apipedidos.application.usecase.listarpedidosusecase.ListarPedidosUseCase;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class ListarPedidosController {

    private final ListarPedidosUseCase listarPedidosUseCase;

    public ListarPedidosController(ListarPedidosUseCase listarPedidosUseCase) {
        this.listarPedidosUseCase = listarPedidosUseCase;
    }

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestHeader("correlationId") String correlationId
    ) {
        try {
            List<Pedido> pedidos = listarPedidosUseCase.listar();

            ListarPedidosResponseV1 listarPedidosResponseV1 = new ListarPedidosResponseV1(pedidos);
            return ResponseEntity.ok(listarPedidosResponseV1);
        } catch (PedidosInativosException e) {

            ErrorResponseV1.DataError dataError = new ErrorResponseV1.DataError(
                    CodigoErro.PIE.getCodigo(),
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
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
