package com.backend.bank.entity;

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
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "token", nullable = false)
    String token;

    @Column(name = "new_phone_number", nullable = false)
    String newPhoneNumber;

    @Column(name = "old_phone_number", nullable = false)
    String oldPhoneNumber;

    @Timestamp
    @Column(name = "expiry_date", nullable = false)
    LocalDateTime expiryDate;
}
