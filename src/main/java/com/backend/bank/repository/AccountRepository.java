package com.backend.bank.repository;

import com.backend.bank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByAccountNumber(String accountNumber);

    Optional<Account> findByAccountNumber(String transferToAccount);
}