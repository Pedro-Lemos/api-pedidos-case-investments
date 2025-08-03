package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.controller.handler;

import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.CodigoErro;
import br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.model.ErrorResponseV1;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseV1> handleInvalidJson(HttpMessageNotReadableException e) {
        ErrorResponseV1.DataError dataError = new ErrorResponseV1.DataError(
                CodigoErro.VLD.getCodigo(),
                "Formato do JSON inválido"
        );
        return ResponseEntity.badRequest().body(new ErrorResponseV1(dataError));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponseV1> handleMissingHeader(MissingRequestHeaderException e) {
        ErrorResponseV1.DataError dataError = new ErrorResponseV1.DataError(
                CodigoErro.VLD.getCodigo(),
                "Header obrigatório ausente: " + e.getHeaderName()
        );
        return ResponseEntity.badRequest().body(new ErrorResponseV1(dataError));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseV1> handleValidationExceptions(MethodArgumentNotValidException e) {
        StringBuilder message = new StringBuilder("Campos obrigatórios ausentes ou inválidos: ");

        e.getBindingResult().getFieldErrors().forEach(error ->
                message.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ")
        );

        ErrorResponseV1.DataError dataError = new ErrorResponseV1.DataError(
                CodigoErro.VLD.getCodigo(),
                message.toString()
        );

        return ResponseEntity.badRequest().body(new ErrorResponseV1(dataError));
    }
}