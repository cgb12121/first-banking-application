package com.backend.bank.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    private String email;
    private String phoneNumber;
    private String password;
    private String firstName;
    private String lastName;
    private AccountRequest account;
    private List<CardRequest> card;
}
