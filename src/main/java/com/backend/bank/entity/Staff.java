package com.backend.bank.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "staff")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @OneToOne(mappedBy = "staff", cascade = CascadeType.ALL, optional = false)
    private StaffAccount staffAccount;

    @PostPersist
    @PostUpdate
    private void updateStaffAccountName() {
        if (this.staffAccount != null) {
            this.staffAccount.setStaffName(this.firstName + " " + this.lastName);
        }
    }
}
