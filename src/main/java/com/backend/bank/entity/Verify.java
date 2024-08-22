    package com.backend.bank.entity;

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
        @Column(name = "id", nullable = false)
        Long id;

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
