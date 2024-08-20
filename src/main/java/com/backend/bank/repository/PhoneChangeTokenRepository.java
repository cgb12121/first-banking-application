package com.backend.bank.repository;

import com.backend.bank.entity.PhoneChangeToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneChangeTokenRepository extends JpaRepository<PhoneChangeToken, Long> {
    Optional<PhoneChangeToken> findByToken(String token);
}
