package com.backend.bank.entity;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "verify")
public class Verify {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "verify_link", nullable = false)
    private String verifyLink;

    @Timestamp
    @Column(name = "created_date", nullable = false)
    private Date createDate;

    @Timestamp
    @Column(name = "expiry_date", nullable = false)
    private Date expiryDate;

    @OneToOne(cascade = CascadeType.ALL)
    private Customer customer;
}
