package com.backend.bank.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @JsonIgnore
    private Long id;

    private String firstName;

    private String middleName;

    private String lastName;

    private String gender;

    private Address address;

    private String phoneNumber;

    private Account account;
}
