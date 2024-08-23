package com.backend.bank.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.bind.DefaultValue;

public record LoanApprovalRequest(

        @NotNull(message = "User's id can not be null")
        Long loanId,

        @DefaultValue(value = "false")
        @NotNull(message = "wtf, it should be false")
        boolean approve,

        @Nullable
        String comments) {
}
