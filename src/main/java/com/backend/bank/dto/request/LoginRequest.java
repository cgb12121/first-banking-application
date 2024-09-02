package com.backend.bank.dto.request;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(

        @NotNull(message = "Please enter your email!")
        String email,

        @NotNull(message = "Please enter your password!")
        String password) {
}
