package com.backend.bank.exception;

public class InvalidVerifyLinkException extends RuntimeException {
    public InvalidVerifyLinkException(String message) {
        super(message);
    }
}
