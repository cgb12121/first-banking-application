package com.backend.bank.repository;

import com.backend.bank.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsByCardNumber(String cardNumber);
}