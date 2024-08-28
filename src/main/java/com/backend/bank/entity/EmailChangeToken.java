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
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "email_change_token")
public class EmailChangeToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Convert(converter = EncryptIdAttributeConverter.class)
    @Column(name = "id", unique = true, nullable = false)
    Long id;

    @Convert(converter = EncryptionAttributeConverter.class)
    @Column(name = "token", nullable = false)
    String token;

    @Convert(converter = EncryptionAttributeConverter.class)
    @Column(name = "new_email", nullable = false)
    String newEmail;

    @Convert(converter = EncryptionAttributeConverter.class)
    @Column(name = "old_email", nullable = false)
    String oldEmail;

    @Timestamp
    @Column(name = "expiry_date", nullable = false)
    LocalDateTime expiryDate;
}
