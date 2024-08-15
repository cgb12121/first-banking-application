package com.backend.bank.dto.request;

import java.math.BigDecimal;
import com.backend.bank.entity.constant.AccountStatus;
import com.backend.bank.entity.constant.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AccountRequest(

        @NotNull(message = "Account number is required")
        @NotBlank(message = "This field can not be blank or contain any white space")
        String accountNumber,

        @NotNull(message = "Balance is required")
        @NotBlank(message = "This field can not be blank or contain any white space")
        @PositiveOrZero(message = "Balance must be positive")
        BigDecimal balance,

        @NotNull(message = "Account type is required", groups = AccountType.class)
        @NotBlank(message = "This field can not be blank or contain any white space")
        AccountType accountType,

        @NotNull(message = "Account status is required",groups = {AccountStatus.class})
        @NotBlank(message = "This field can not be blank or contain any white space")
        AccountStatus accountStatus) {
}