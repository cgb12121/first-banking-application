package com.backend.bank.entity;

import com.backend.bank.entity.constant.AccountStatus;
import com.backend.bank.entity.constant.AccountType;

import com.backend.bank.security.data.EncryptIdAttributeConverter;
import com.backend.bank.security.data.EncryptionAttributeConverter;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Convert(converter = EncryptIdAttributeConverter.class)
    @Column(name = "id", unique = true, nullable = false)
    Long id;

//    @Convert(converter = EncryptionAttributeConverter.class)
    @Column(name = "account_number", nullable = false, unique = true)
    String accountNumber;

    @Column(name = "balance", nullable = false)
    BigDecimal balance;

    @Column(name = "interest", nullable = false)
    BigDecimal interest;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    AccountStatus accountStatus;

    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customer accountHolder;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Transaction> transactions;
}
