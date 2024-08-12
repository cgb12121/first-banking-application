package com.backend.bank.dto.request;

public record ChangePhoneNumberRequest(
        String oldPhoneNumber,
        String newPhoneNumber,
        String confirmPassword) {
}
