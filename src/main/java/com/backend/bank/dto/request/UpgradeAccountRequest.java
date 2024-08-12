package com.backend.bank.dto.request;

import com.backend.bank.entity.constant.AccountType;

public record UpgradeAccountRequest(
        Long customerId,
        AccountType newAccountType) {
}
