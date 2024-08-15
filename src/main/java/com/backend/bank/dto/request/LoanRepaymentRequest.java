package com.backend.bank.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

//TODO: implement new changes to the LoanServiceImpl class
public record LoanRepaymentRequest(

        @NotNull(message = "User's loan ID can not be null")
        Long loanId,

        @NotNull(message = "Loan account number can not be empty")
        String loanAccountNumber,

        @NotNull(message = "Repay amount can not be empty")
        @Positive(message = "Repay amount must be greater than 0")
        @Min(value = 10000, message = "Repayment amount must be at least 10000")
        BigDecimal repaymentAmount,

        @NotNull(message = "Repayment date is required")
        @Future(message = "Repayment date must be in the future")
        LocalDate repaymentDate) {
}