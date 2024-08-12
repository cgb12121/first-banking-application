package com.backend.bank.dto.request;

public record UpdateCustomerInfoRequest(
        Long customerId,
        String firstName,
        String lastName,
        String email,
        String phoneNumber) {
}
