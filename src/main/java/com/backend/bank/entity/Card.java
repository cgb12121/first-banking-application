package com.backend.bank.entity;

import com.backend.bank.entity.enums.CardType;

import com.backend.bank.security.data.EncryptIdAttributeConverter;
import com.backend.bank.security.data.EncryptionAttributeConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Convert(converter = EncryptIdAttributeConverter.class)
    @Column(name = "id", unique = true, nullable = false)
    Long id;

    @Convert(converter = EncryptionAttributeConverter.class)
    @Column(name = "card_number", nullable = false, unique = true)
    String cardNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false)
    CardType cardType;

    @Column(name = "expiry_date", nullable = false)
    LocalDate expiryDate;

    @Column(name = "credit_limit", nullable = false)
    BigDecimal creditLimit;

    @Column(name = "balance", nullable = false)
    BigDecimal balance;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "customer_id", nullable = false)
    Customer customer;
}
