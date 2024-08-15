package com.backend.bank.dto.request;

import com.backend.bank.entity.Customer;
import com.backend.bank.entity.constant.CardType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegisterNewCardRequest(
        @NotNull(message = "User's id must not be null or empty")
        Long customerId,

        @NotNull(message = "Card number must not be null or empty")
        @Pattern(regexp = "^\\d{16}$", message = "Invalid card number format")
        String cardNumber,

        @NotNull(message = "Unknown Card type or empty card type")
        CardType cardType,

        @DefaultValue("0")
        @Min(value = 0, message = "Credit limit cannot be negative")
        @Max(value = 999999999999L, message = "Credit limit exceeds maximum value")
        BigDecimal creditLimit,

        @NotNull(message = "Expire date must be some day in the future")
        @PastOrPresent(message = "Expiration date cannot be in the past")
        LocalDate expirationDate,

        @NotNull(message = "There must be a card holder")
        @ManyToOne Customer customer) {
}
