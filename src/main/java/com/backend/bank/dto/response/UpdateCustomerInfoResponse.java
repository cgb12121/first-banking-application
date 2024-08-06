package com.backend.bank.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomerInfoResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String message;
}
