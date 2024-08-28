package com.backend.bank.dto.response;

import com.backend.bank.entity.enums.TransactionStatus;
import com.backend.bank.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        LocalDateTime timestamp,
        TransactionType type,
        TransactionStatus status,
        String transferToAccount) {
}
