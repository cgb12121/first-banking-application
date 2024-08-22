package com.backend.bank.entity.constant;

import lombok.*;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER"),
    STAFF("ROLE_STAFF"),
    MANAGER("ROLE_MANAGER"),
    ADMIN("ROLE_ADMIN"),;

    private final String role;
}