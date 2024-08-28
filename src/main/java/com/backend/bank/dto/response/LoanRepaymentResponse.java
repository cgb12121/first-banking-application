package com.backend.bank.dto.response;

import com.backend.bank.entity.enums.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LoanRepaymentResponse(
        Long loanId,
        BigDecimal repaymentAmount,
        BigDecimal remainingAmount,
        LocalDateTime repaymentDate,
        LoanStatus loanStatus,
        String message) {
}
