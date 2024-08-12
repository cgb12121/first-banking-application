package com.backend.bank.dto.request;

public record UpdatePhoneNumberRequest(
        String email,
        String newPhoneNumber) {
}
