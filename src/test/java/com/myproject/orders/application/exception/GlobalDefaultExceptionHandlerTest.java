package com.myproject.orders.application.exception;

import com.myproject.orders.domain.exception.PedidoDuplicadoException;
import com.myproject.orders.domain.exception.RecursoJaExisteException;
import com.myproject.orders.domain.exception.RecursoNaoEncontratoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalDefaultExceptionHandlerTest {

    private GlobalDefaultExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalDefaultExceptionHandler();
    }

    @Test
    @DisplayName("Deve tratar RecursoNaoEncontratoException e retornar status 404 NOT_FOUND")
    void handleResourceNotFound_DeveRetornarNotFound() {
        String errorMessage = "Registro não encontrado com código 123";
        RecursoNaoEncontratoException exception = new RecursoNaoEncontratoException(errorMessage);

        ResponseEntity<ProblemDetail> response = handler.handleResourceNotFound(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getTitle()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
        assertThat(problemDetail.getDetail()).isEqualTo(errorMessage);
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Deve tratar RecursoJaExisteException e retornar status 400 BAD_REQUEST")
    void handleResourceBadRequest_DeveRetornarBadRequest() {
        String errorMessage = "Recurso 'X' já existe no sistema.";
        RecursoJaExisteException exception = new RecursoJaExisteException(errorMessage);

        ResponseEntity<ProblemDetail> response = handler.handleResourceBadRequest(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
        assertThat(problemDetail.getDetail()).isEqualTo(errorMessage);
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deve tratar PedidoDuplicadoException e retornar status 409 CONFLICT")
    void handleResourceConflict_DeveRetornarConflic() {
        String errorMessage = "Pedido com ID Externo PEDIDO-EXT-001 já existe no sistema.";
        PedidoDuplicadoException exception = new PedidoDuplicadoException(errorMessage);

        ResponseEntity<ProblemDetail> response = handler.handleResourceConflict(exception);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        ProblemDetail problemDetail = response.getBody();
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getTitle()).isEqualTo(HttpStatus.CONFLICT.getReasonPhrase());
        assertThat(problemDetail.getDetail()).isEqualTo(errorMessage);
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }
}
