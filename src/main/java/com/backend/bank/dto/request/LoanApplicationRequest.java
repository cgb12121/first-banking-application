package com.backend.bank.dto.request;

import java.math.BigDecimal;

public record LoanApplicationRequest(
        Long customerId,
        BigDecimal amount,
        BigDecimal interestRate,
        int termInMonths) {
}
