package com.backend.bank.exception;

public class UnknownTransactionTypeException extends Exception {
    public UnknownTransactionTypeException(String message) {
        super(message);
    }
}
