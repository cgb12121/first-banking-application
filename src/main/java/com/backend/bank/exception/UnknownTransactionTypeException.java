package com.backend.bank.exception;

public class UnknownTransactionTypeException extends RuntimeException {
    public UnknownTransactionTypeException(String message) {
        super(message);
    }
}
