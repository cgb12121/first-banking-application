package com.backend.bank.exception;

public class InvalidLoanStatusException extends Exception {
    public InvalidLoanStatusException(String message) {
        super(message);
    }
}
