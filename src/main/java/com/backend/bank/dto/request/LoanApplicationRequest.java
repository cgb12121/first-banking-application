package com.backend.bank.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record LoanApplicationRequest(

        @NotNull(message = "Customer's Id is null")
        Long customerId,

        @NotNull(message = "The amount of the loan can not be empty")
        @Positive(message = "The amount of the loan must be a positive number")
        BigDecimal amount,

        @NotNull(message = "The interest rate of the loan can not be empty")
        @Positive(message = "The interest rate must be a positive number")
        BigDecimal interestRate,

        @NotNull(message = "The term of loan must not be null")
        @Positive(message = "The term of loan must be a positive number")
        @Min(value = 3)
        @Max(value = 60)
        int termInMonths) {
}
