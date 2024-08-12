package com.backend.bank.dto.request;

import java.math.BigDecimal;

public record LoanRepaymentRequest(
        Long loanId,
        BigDecimal repaymentAmount) {
}
