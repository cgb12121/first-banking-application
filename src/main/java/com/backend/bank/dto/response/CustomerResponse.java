package com.backend.bank.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CustomerResponse {
    private Long id;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private LocalDateTime createdDate;
}
