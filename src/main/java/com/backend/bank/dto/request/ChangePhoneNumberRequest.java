package com.backend.bank.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePhoneNumberRequest(

        @NotNull(message = "You must provide your old phone number")
        String oldPhoneNumber,

        @NotNull(message = "You must provide new phone number")
        @Size(min = 8, max = 14, message = "Phone number must have more than 8 digits and less than 14 digits")
        @Pattern(regexp = "^\\d+$", message = "Phone number must contain only digits")
        String newPhoneNumber,

        @NotNull(message = "Please enter your password")
        String confirmPassword) {
}
