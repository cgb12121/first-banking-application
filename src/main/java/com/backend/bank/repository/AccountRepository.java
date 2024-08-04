package com.backend.bank.repository;

import com.backend.bank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByAccountNumber(String accountNumber);
}