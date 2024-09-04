package com.backend.bank.exception;

public class InvalidSecretTokenException extends RuntimeException {
    public InvalidSecretTokenException(String message) {
        super(message);
    }
}
