package com.backend.bank.dto.request;

public record LoginRequest(
        String identifier,
        String password) {
}
