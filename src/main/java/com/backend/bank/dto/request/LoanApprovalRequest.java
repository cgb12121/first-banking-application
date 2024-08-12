package com.backend.bank.dto.request;

import lombok.Value;

/**
 * @param approve  true for approval, false for rejection
 * @param comments Optional comments
 */
public record LoanApprovalRequest(
        Long loanId,
        boolean approve,
        String comments) {
}
