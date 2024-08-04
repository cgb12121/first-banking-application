package com.backend.bank.repository;

import com.backend.bank.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByAccount_AccountNumber(String accountNumber);

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    Optional<Customer> findByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    UserDetails loadByUserName(String email);
}