package com.backend.bank.dto.response;

import com.backend.bank.entity.constant.AccountType;

public record UpgradeAccountResponse(String message, AccountType newAccountType) {
}
