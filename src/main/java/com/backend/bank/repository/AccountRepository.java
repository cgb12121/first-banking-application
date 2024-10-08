package com.backend.bank.repository;

import com.backend.bank.entity.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByAccountNumber(String accountNumber);

    Optional<Account> findByAccountNumber(String transferToAccount);

    Optional<Account> findByAccountHolder_Email(String accountHolder_email);

    Optional<Account> findByAccountHolder_PhoneNumber(String phoneNumber);

    Optional<Account> findByAccountHolder_Id(Long id);
}