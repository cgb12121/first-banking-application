package com.backend.bank.dto.response;

public record UpdateCustomerInfoResponse(String firstName, String lastName, String email, String phoneNumber,
                                         String message) {
}
