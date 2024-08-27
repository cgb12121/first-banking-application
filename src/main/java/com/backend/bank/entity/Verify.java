package com.backend.bank.entity;

import com.backend.bank.security.data.EncryptIdAttributeConverter;
import com.backend.bank.security.data.EncryptionAttributeConverter;
import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "verify")
public class Verify {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Convert(converter = EncryptIdAttributeConverter.class)
    @Column(name = "id", unique = true, nullable = false)
    Long id;

    @Convert(converter = EncryptionAttributeConverter.class)
    @Column(name = "verify_link", nullable = false)
    String verifyLink;

    @Timestamp
    @Column(name = "created_date", nullable = false)
    LocalDateTime createDate;

    @Timestamp
    @Column(name = "expiry_date", nullable = false)
    LocalDateTime expiryDate;

    @OneToOne(cascade = CascadeType.ALL)
    Customer customer;
}
