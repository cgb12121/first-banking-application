package com.backend.bank.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomerInfoRequest {
    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
