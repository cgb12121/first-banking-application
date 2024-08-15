package com.backend.bank.dto.request;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(

        @NotNull(message = "Please provide your identifier such as: email, phone number or account number")
        String identifier,

        @NotNull(message = "Please enter your password")
        String password) {
}
