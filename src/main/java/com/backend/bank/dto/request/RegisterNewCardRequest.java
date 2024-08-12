package com.backend.bank.dto.request;

import com.backend.bank.entity.Customer;
import com.backend.bank.entity.constant.CardType;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;

public record RegisterNewCardRequest(
        Long customerId,
        String cardNumber,
        CardType cardType,
        BigDecimal creditLimit,
        String expirationDate,
        @ManyToOne Customer customer) {
}
