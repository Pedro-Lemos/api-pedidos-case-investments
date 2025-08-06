package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.listarpedidos;

import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.CodigoErro;
import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.model.ErrorResponseV1;
import br.com.pedro.lemos.apipedidos.application.usecase.listarpedidosusecase.ListarPedidosUseCase;
import br.com.pedro.lemos.apipedidos.domain.entity.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos")
public class ListarPedidosController {

    private final ListarPedidosUseCase listarPedidosUseCase;

    public ListarPedidosController(ListarPedidosUseCase listarPedidosUseCase) {
        this.listarPedidosUseCase = listarPedidosUseCase;
    }

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestHeader("correlationId") String correlationId,
            Pageable pageable
    ) {
        try {
            Page<Pedido> pedidosPage = listarPedidosUseCase.listar(pageable);

            if (pedidosPage.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(pedidosPage);
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