package com.backend.bank.dto.request;

import jakarta.validation.constraints.*;

public record UpdateCustomerInfoRequest(

        @NotNull(message = "Who there?")
        Long customerId,

        @NotNull
        String firstName,

        @NotNull
        String lastName,

        @NotNull(message = "You must provide your new email address")
        @Email(message = "Please enter a valid email address")
        @NotBlank(message = "This field can not be blank or contain any white space")
        @Size(min = 5, max = 100, message = "Email length must be between 5 and 100 characters")
        @Pattern(regexp = "^[\\w-.]+@(gmail)\\.(com|co\\.vn|co\\.us|etc)$", message = "Invalid Google email")
        String email,

        @NotNull(message = "You must provide new phone number")
        @Size(min = 8, max = 14, message = "Phone number must have more than 8 digits and less than 14 digits")
        @Pattern(regexp = "^\\d+$", message = "Phone number must contain only digits")
        String phoneNumber) {
}
