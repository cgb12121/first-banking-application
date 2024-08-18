package com.backend.bank.exception;

public class IllegalAccountTypeException extends RuntimeException {
    public IllegalAccountTypeException(String message) {
        super(message);
    }
}
