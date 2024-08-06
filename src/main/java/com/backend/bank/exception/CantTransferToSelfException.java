package com.backend.bank.exception;

public class CantTransferToSelfException extends Exception {
    public CantTransferToSelfException(String message) {
        super(message);
    }
}
