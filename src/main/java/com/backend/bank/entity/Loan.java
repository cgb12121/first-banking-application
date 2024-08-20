package com.backend.bank.entity;

import com.backend.bank.entity.constant.LoanStatus;
import com.backend.bank.entity.constant.TakeLoanStatus;
import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "interest_amount")
    private BigDecimal interestAmount;

    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Timestamp
    @Column(name = "late_date")
    private LocalDateTime lateDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "take_loan_status", nullable = false)
    private TakeLoanStatus takeLoanStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_status")
    private LoanStatus loanStatus;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}
