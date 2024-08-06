package com.backend.bank.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterNewCardResponse {
    private String cardType;
    private String cardNumber;
    private String expirationDate;
    private String message;
}
