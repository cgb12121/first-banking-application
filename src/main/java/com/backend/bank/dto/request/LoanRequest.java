package com.backend.bank.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.backend.bank.entity.constant.TakeLoanStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanRequest {
    private BigDecimal amount;
    private BigDecimal interestRate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private TakeLoanStatus takeLoanStatus;
    private Long customerId;
}

