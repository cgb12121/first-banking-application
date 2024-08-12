package com.backend.bank.dto.request;

public record ChangeEmailRequest(
        String oldEmail,
        String newEmail,
        String confirmPassword) {
}
