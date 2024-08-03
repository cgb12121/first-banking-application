package com.backend.bank.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequest {
    private String password;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
}

