package com.backend.bank.exception;

public class InvalidRepaymentAmountException extends Exception {
    public InvalidRepaymentAmountException(String message) {
        super(message);
    }
}
