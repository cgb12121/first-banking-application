package com.backend.bank.entity;

import com.backend.bank.security.data.EncryptionAttributeConverter;
import com.backend.bank.security.data.IdAttributeConverter;
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
@Table(name = "phone_change_token")
public class PhoneChangeToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Convert(converter = IdAttributeConverter.class)
    @Column(name = "id", unique = true, nullable = false)
    Long id;

    @Convert(converter = EncryptionAttributeConverter.class)
    @Column(name = "token", nullable = false)
    String token;

    @Convert(converter = EncryptionAttributeConverter.class)
    @Column(name = "new_phone_number", nullable = false)
    String newPhoneNumber;

    @Convert(converter = EncryptionAttributeConverter.class)
    @Column(name = "old_phone_number", nullable = false)
    String oldPhoneNumber;

    @Timestamp
    @Column(name = "expiry_date", nullable = false)
    LocalDateTime expiryDate;
}
