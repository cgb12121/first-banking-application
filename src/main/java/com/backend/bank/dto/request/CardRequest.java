package com.backend.bank.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.backend.bank.entity.constant.CardType;

public record CardRequest(
        String cardNumber,
        CardType cardType,
        LocalDate expiryDate,
        BigDecimal creditLimit,
        BigDecimal balance) {
}
