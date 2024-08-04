package com.backend.bank.dto.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String message;
    private String token;
}
