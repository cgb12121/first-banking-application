package com.backend.bank.entity;

import com.backend.bank.entity.constant.StaffAccountStatus;
import com.backend.bank.entity.constant.StaffRole;
import jakarta.persistence.*;
import lombok.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "staff_account")
public class StaffAccount implements UserDetails  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", referencedColumnName = "id")
    private Staff staff;

    @Column(name = "staff_name", nullable = false)
    private String staffName;

    @Column(name = "staff_account", nullable = false, unique = true)
    private String staffAccount;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false, columnDefinition = "ENABLED")
    private StaffAccountStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private StaffRole role;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getRole()));
    }

    @Override
    public String getUsername() {
        return this.staffAccount;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isEnabled() {
        return status == StaffAccountStatus.ENABLED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status == StaffAccountStatus.ENABLED;
    }

    @PrePersist
    @PreUpdate
    public void getStaffName() {
        if (this.staff != null) {
            this.staffName = this.staff.getFirstName() + " " + this.staff.getLastName();
        }
    }
}
