package com.backend.bank.dto.request;

import com.backend.bank.entity.constant.TransactionType;

import java.math.BigDecimal;

public record TransactionRequest(
        BigDecimal amount,
        TransactionType type,
        String transferToAccount) {
}
