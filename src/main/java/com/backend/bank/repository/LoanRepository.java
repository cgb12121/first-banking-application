package com.backend.bank.repository;

import com.backend.bank.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}