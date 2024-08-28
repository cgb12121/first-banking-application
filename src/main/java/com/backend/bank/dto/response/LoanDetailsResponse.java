package com.backend.bank.dto.response;

import com.backend.bank.entity.enums.LoanStatus;
import com.backend.bank.entity.enums.TakeLoanStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LoanDetailsResponse(
        Long loanId,
        BigDecimal amount,
        BigDecimal interestRate,
        BigDecimal interestAmount,
        LocalDateTime startDate,
        LocalDateTime endDate,
        LoanStatus loanStatus,
        TakeLoanStatus takeLoanStatus,
        BigDecimal remainingAmount) {
}
