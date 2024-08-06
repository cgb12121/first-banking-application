package com.backend.bank.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    private String email;
    private String oldPassword;
    private String newPassword;
}
