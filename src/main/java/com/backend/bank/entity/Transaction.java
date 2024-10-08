package com.backend.bank.entity;

import com.backend.bank.entity.enums.TransactionStatus;
import com.backend.bank.entity.enums.TransactionType;
import com.backend.bank.security.data.EncryptIdAttributeConverter;
import com.backend.bank.security.data.EncryptionAttributeConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Convert(converter = EncryptIdAttributeConverter.class)
    @Column(name = "id", unique = true, nullable = false)
    Long id;

    @Column(name = "amount", nullable = false)
    BigDecimal amount;

    @Column(name = "timestamp", nullable = false)
    LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    TransactionStatus status;

    @JsonIgnore
    @Convert(converter = EncryptionAttributeConverter.class)
    @Column(name = "transfer_to_account")
    String transferToAccount;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "account_id", nullable = false)
    Account account;
}
