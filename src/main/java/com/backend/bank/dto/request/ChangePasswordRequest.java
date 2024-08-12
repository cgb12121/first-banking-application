package com.backend.bank.dto.request;

public record ChangePasswordRequest(
        String email,
        String oldPassword,
        String newPassword) {
}
