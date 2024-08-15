package com.backend.bank.dto.request;

import com.backend.bank.entity.constant.TransactionType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionRequest(

        @NotNull(message = "The amount of transaction must not be empty")
        @Min(value = 1, message = "The amount must be greater than 0")
        @Max(value = 100000000, message = "The total amount in one transaction can not be greater or more than 100 million")
        BigDecimal amount,

        @NotNull(message = "Missing transaction type")
        TransactionType type,

        @NotNull(message = "Who? Who are you making transaction with?")
        String transferToAccount) {
}
