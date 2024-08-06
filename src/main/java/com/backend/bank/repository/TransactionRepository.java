package com.backend.bank.repository;

import com.backend.bank.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);
    Page<Transaction> findByTransferToAccount(String accountNumber, Pageable pageable);
}