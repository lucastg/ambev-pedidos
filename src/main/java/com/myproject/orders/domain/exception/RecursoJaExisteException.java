package com.myproject.orders.domain.exception;

public class RecursoJaExisteException extends RuntimeException{
    public RecursoJaExisteException(String message) {
        super(message);
    }
}
