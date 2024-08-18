package com.backend.bank.exception;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
