package com.backend.bank.dto.response;

public record RegisterNewCardResponse(String cardType, String cardNumber, String expirationDate, String message) {
}
