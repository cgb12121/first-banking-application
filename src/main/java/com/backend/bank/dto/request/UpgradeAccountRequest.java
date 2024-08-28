package com.backend.bank.dto.request;

import com.backend.bank.entity.enums.AccountType;

import jakarta.validation.constraints.NotNull;

public record UpgradeAccountRequest(

        @NotNull(message = "User's id can not be null")
        Long customerId,

        @NotNull(message = "Account type must not be empty or null")
        AccountType newAccountType) {
}
