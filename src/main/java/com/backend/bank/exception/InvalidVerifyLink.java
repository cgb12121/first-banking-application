package com.backend.bank.exception;

public class InvalidVerifyLink extends RuntimeException {
    public InvalidVerifyLink(String message) {
        super(message);
    }
}
