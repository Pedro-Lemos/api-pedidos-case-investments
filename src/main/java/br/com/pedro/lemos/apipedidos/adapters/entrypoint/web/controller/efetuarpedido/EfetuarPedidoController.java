package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.efetuarpedido;

import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.CodigoErro;
import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.efetuarpedido.request.EfetuarPedidoRequestV1;
import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.efetuarpedido.response.EfetuarPedidoResponseV1;
import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.model.ErrorResponseV1;
import br.com.pedro.lemos.apipedidos.application.exception.DadosProdutoInconsistentesException;
import br.com.pedro.lemos.apipedidos.application.exception.PedidoEmAndamentoException;
import br.com.pedro.lemos.apipedidos.application.exception.ProdutoNaoDisponivelException;
import br.com.pedro.lemos.apipedidos.application.exception.ProdutoNaoEncontradoException;
import br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase.EfetuarPedidoUseCase;
import br.com.pedro.lemos.apipedidos.application.usecase.efetuarpedidousecase.model.SolicitacaoEfetuarPedido;
import br.com.pedro.lemos.apipedidos.domain.entity.Produto;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class EfetuarPedidoController {
    private final EfetuarPedidoUseCase efetuarPedidoUseCase;

    public EfetuarPedidoController(EfetuarPedidoUseCase efetuarPedidoUseCase) {
        this.efetuarPedidoUseCase = efetuarPedidoUseCase;
    }

    @PostMapping
    public ResponseEntity<?> efetuarPedido(
            @Valid
            @RequestHeader("transactionId") String transactionId,
            @RequestHeader("correlationId") String correlationId,
            @Valid @RequestBody EfetuarPedidoRequestV1 request) {

        try {


            List<Produto> produtos = request.getProdutos().stream()
                    .map(EfetuarPedidoRequestV1.ProdutoRequest::toProduto)
                    .toList();

            SolicitacaoEfetuarPedido solicitacao = new SolicitacaoEfetuarPedido(
                    request.getCodigoIdentificacaoCliente(),
                    produtos,
                    transactionId
            );

            Long idPedido = efetuarPedidoUseCase.efetuar(solicitacao);

            EfetuarPedidoResponseV1.DataResponse data = new EfetuarPedidoResponseV1.DataResponse(
                    "Pedido efetuado com sucesso!",
                    idPedido
            );
            EfetuarPedidoResponseV1 response = new EfetuarPedidoResponseV1(data);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);

        } catch (PedidoEmAndamentoException e) {

            ErrorResponseV1.DataError dataError = new ErrorResponseV1.DataError(
                    CodigoErro.PEM.getCodigo(),
                    e.getMessage()
            );

            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ErrorResponseV1(dataError));

        } catch (ProdutoNaoDisponivelException e) {

            ErrorResponseV1.DataError dataError = new ErrorResponseV1.DataError(
                    CodigoErro.PND.getCodigo(),
                    e.getMessage()
            );

            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ErrorResponseV1(dataError));

        } catch (ProdutoNaoEncontradoException e) {

            ErrorResponseV1.DataError dataError = new ErrorResponseV1.DataError(
                    CodigoErro.PNE.getCodigo(),
                    e.getMessage()
            );

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseV1(dataError));

        } catch (DadosProdutoInconsistentesException e) {

            ErrorResponseV1.DataError dataError = new ErrorResponseV1.DataError(
                    CodigoErro.DPI.getCodigo(),
                    e.getMessage()
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
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