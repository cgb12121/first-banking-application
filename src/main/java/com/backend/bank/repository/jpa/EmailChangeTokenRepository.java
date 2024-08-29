package com.backend.bank.repository.jpa;

import com.backend.bank.entity.EmailChangeToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailChangeTokenRepository extends JpaRepository<EmailChangeToken, Long> {
    Optional<Object> findByToken(String token);
}