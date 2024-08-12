package com.backend.bank.repository;

import com.backend.bank.entity.Verify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerifyRepository extends JpaRepository<Verify, Long> {
     boolean existsByVerifyLink(String verifyLink);
     Optional<Verify> findByVerifyLink(String verifyLink);
}