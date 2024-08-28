package com.backend.bank.dto.response;

import com.backend.bank.entity.enums.AccountType;

public record UpgradeAccountResponse(String message, AccountType newAccountType) {
}
