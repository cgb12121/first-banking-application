package com.backend.bank.entity.enums;

import lombok.*;

@Getter
@RequiredArgsConstructor
public enum Role {
    ROLE_USER("ROLE_USER"),
    ROLE_STAFF("ROLE_STAFF"),
    ROLE_MANAGER("ROLE_MANAGER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private final String role;
}