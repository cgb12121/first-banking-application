package com.backend.bank.repository;

import com.backend.bank.entity.Customer;
import com.backend.bank.entity.Transaction;
import com.backend.bank.entity.constant.TransactionType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);

    Page<Transaction> findAllByAccount_AccountHolder(Customer account_accountHolder, Pageable pageable);

    Page<Transaction> findAllByTypeAndAccount_AccountHolder(TransactionType type, Customer account_accountHolder, Pageable pageable);

    Page<Transaction> findAllByTransferToAccount(String transferToAccount, Pageable pageable);

    Page<Transaction> findAllByIdAndTransferToAccount(Long id, String transferToAccount, Pageable pageable);

    Page<Transaction> findByTransferToAccount(String accountNumber, Pageable pageable);

    Transaction findById(long id);

    Page<Transaction> findByAccount_AccountNumber(String account_accountNumber, Pageable pageable);
}