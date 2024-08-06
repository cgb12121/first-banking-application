package com.backend.bank.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePhoneNumberRequest {
    private String email;
    private String newPhoneNumber;
}
