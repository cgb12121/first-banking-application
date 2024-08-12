package com.backend.bank.dto.request;

import java.util.List;

public record SignupRequest(
        String email,
        String phoneNumber,
        String password,
        String firstName,
        String lastName,
        AccountRequest account,
        List<CardRequest> card) {
}
