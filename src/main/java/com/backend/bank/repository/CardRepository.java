package com.backend.bank.repository;

import com.backend.bank.entity.Card;
import com.backend.bank.entity.constant.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsByCardNumber(String cardNumber);

    long countByCardType(CardType cardType);
}