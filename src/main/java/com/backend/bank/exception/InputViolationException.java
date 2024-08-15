package com.backend.bank.exception;

public class InputViolationException extends RuntimeException {
    public InputViolationException(String message) { super(message); }
}
