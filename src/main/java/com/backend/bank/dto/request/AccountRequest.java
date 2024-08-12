package com.backend.bank.dto.request;

import java.math.BigDecimal;
import com.backend.bank.entity.constant.AccountStatus;
import com.backend.bank.entity.constant.AccountType;

public record AccountRequest(
        String accountNumber,
        BigDecimal balance,
        AccountType accountType,
        AccountStatus accountStatus) {
}
