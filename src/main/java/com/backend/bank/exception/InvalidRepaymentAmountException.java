package com.backend.bank.exception;

public class InvalidRepaymentAmountException extends RuntimeException {
    public InvalidRepaymentAmountException(String message) {
        super(message);
    }
}
