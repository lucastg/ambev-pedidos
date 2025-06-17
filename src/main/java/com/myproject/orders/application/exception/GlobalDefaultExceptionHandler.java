package com.myproject.orders.application.exception;

import com.myproject.orders.domain.exception.PedidoDuplicadoException;
import com.myproject.orders.domain.exception.RecursoJaExisteException;
import com.myproject.orders.domain.exception.RecursoNaoEncontratoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalDefaultExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontratoException.class)
    protected ResponseEntity<ProblemDetail> handleResourceNotFound(RecursoNaoEncontratoException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle(HttpStatus.NOT_FOUND.getReasonPhrase());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(RecursoJaExisteException.class)
    protected ResponseEntity<ProblemDetail> handleResourceBadRequest(RecursoJaExisteException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);

    }

    @ExceptionHandler(PedidoDuplicadoException.class)
    protected ResponseEntity<ProblemDetail> handleResourceConflict(PedidoDuplicadoException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle(HttpStatus.CONFLICT.getReasonPhrase());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }
}
