package com.backend.bank.exception;

public class AccountNotExistException extends Exception {
    public AccountNotExistException(String message) {
        super(message);
    }
}
