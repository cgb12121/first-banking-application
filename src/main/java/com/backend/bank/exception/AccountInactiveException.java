package com.backend.bank.exception;

public class AccountInactiveException extends Exception {
    public AccountInactiveException(String message) {
        super(message);
    }
}
