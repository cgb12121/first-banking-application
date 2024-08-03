package com.backend.bank.dto.request;

import java.math.BigDecimal;
import com.backend.bank.entity.constant.TransactionStatus;
import com.backend.bank.entity.constant.TransactionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequest {
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private String transferToAccount;
    private Long accountId;
}

