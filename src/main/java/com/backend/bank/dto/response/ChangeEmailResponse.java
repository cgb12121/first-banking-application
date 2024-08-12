package com.backend.bank.dto.response;

public record ChangeEmailResponse (
        String message,
        String confirmLink) {
}
