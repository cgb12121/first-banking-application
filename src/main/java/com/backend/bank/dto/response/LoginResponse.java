package com.backend.bank.dto.response;

import java.util.List;

public record LoginResponse(List<String> message, String token, String refreshToken) {
    public LoginResponse(List<String> message) {
        this(message, null, null);
    }
}
