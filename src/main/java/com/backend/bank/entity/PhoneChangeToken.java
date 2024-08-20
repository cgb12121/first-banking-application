package com.backend.bank.entity;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "phone_change_token")
public class PhoneChangeToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "new_phone_number", nullable = false)
    private String newPhoneNumber;

    @Column(name = "old_phone_number", nullable = false)
    private String oldPhoneNumber;

    @Timestamp
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
}
