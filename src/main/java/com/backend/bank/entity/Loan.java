package com.backend.bank.entity;

import com.backend.bank.entity.enums.LoanStatus;
import com.backend.bank.entity.enums.TakeLoanStatus;

import com.backend.bank.security.data.EncryptIdAttributeConverter;

import jakarta.persistence.*;

import jdk.jfr.Timestamp;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Convert(converter = EncryptIdAttributeConverter.class)
    @Column(name = "id", unique = true, nullable = false)
    Long id;

    @Column(name = "amount", nullable = false)
    BigDecimal amount;

    @Column(name = "interest_amount")
    BigDecimal interestAmount;

    @Column(name = "interest_rate", nullable = false)
    BigDecimal interestRate;

    @Column(name = "start_date", nullable = false)
    LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    LocalDateTime endDate;

    @Timestamp
    @Column(name = "late_date")
    LocalDateTime lateDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "take_loan_status", nullable = false)
    TakeLoanStatus takeLoanStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_status")
    LoanStatus loanStatus;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customer customer;
}
