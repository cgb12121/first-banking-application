package com.backend.bank.dto.request;

import com.backend.bank.entity.enums.AccountType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AccountRequest(

        @NotNull(message = "Account number is required")
        @NotBlank(message = "This field can not be blank or contain any white space")
        String accountNumber,

        @NotNull(message = "Account type is required", groups = AccountType.class)
        AccountType accountType) {
}