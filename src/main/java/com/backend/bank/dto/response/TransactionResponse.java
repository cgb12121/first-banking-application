package com.backend.bank.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.backend.bank.entity.constant.TransactionStatus;
import com.backend.bank.entity.constant.TransactionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private TransactionType type;
    private TransactionStatus status;
    private String transferToAccount;
    private Long accountId;
}