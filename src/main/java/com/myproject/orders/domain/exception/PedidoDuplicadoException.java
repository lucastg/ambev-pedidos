package com.myproject.orders.domain.exception;

public class PedidoDuplicadoException extends RuntimeException{
    public PedidoDuplicadoException(String message) {
        super(message);
    }
}
