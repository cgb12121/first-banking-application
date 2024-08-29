package com.backend.bank.repository.jpa;

import com.backend.bank.entity.Loan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Page<Loan> findById(Long bankId, Pageable pageable);

    List<Loan> findByCustomerId(Long id);
}