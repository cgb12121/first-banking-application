package com.backend.bank.exception;

public class LoanNotFoundException extends Exception {
    public LoanNotFoundException(String message) {
        super(message);
    }
}
