package com.backend.bank.exception;

public class CantTransferToSelfException extends RuntimeException {
    public CantTransferToSelfException(String message) {
        super(message);
    }
}
