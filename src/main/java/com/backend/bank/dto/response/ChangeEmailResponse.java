package com.backend.bank.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeEmailResponse {
    private String message;
    private String confirmLink;
}
