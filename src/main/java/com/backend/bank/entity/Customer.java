package com.backend.bank.entity;

import com.backend.bank.entity.enums.AccountStatus;
import com.backend.bank.entity.enums.Role;
import com.backend.bank.security.data.EncryptIdAttributeConverter;
import com.backend.bank.security.data.EncryptionAttributeConverter;
import jakarta.persistence.*;

import jdk.jfr.Timestamp;

import lombok.*;

import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "customers")
public class Customer implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Convert(converter = EncryptIdAttributeConverter.class)
    @Column(name = "id", unique = true, nullable = false)
    Long id;

    @Column(name = "password", nullable = false)
    String password;

    @Convert(converter = EncryptionAttributeConverter.class)
    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Convert(converter = EncryptionAttributeConverter.class)
    @Column(name = "phone_number", nullable = false)
    String phoneNumber;

    @Column(name = "first_name", nullable = false)
    String firstName;

    @Column(name = "last_name", nullable = false)
    String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    Role role;

    @Timestamp
    @Column(name = "created_date", nullable = false)
    LocalDateTime createdDate;
    
    @OneToOne(mappedBy = "accountHolder", cascade = CascadeType.ALL)
    Account account;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Loan> loans;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Card> cards;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.getRole().name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isEnabled() {
        AccountStatus status = this.getAccount().getAccountStatus();
        return status == AccountStatus.ACTIVE;
    }

    @Override
    public boolean isAccountNonExpired() {
        AccountStatus status = this.getAccount().getAccountStatus();
        return status != AccountStatus.INACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        AccountStatus status = this.getAccount().getAccountStatus();
        return status != AccountStatus.FROZEN && status != AccountStatus.BANNED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
