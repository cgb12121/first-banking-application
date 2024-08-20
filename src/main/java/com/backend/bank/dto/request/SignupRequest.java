package com.backend.bank.dto.request;

import com.backend.bank.utils.annotation.PasswordConstraint;
import jakarta.validation.constraints.*;

import java.util.List;

public record SignupRequest(

        @NotNull(message = "Email can not be empty")
        @Email(message = "Please enter a valid email address")
        @NotBlank(message = "This field can not be blank or contain any white space")
        @Size(min = 5, max = 100, message = "Email length must be between 5 and 100 characters")
        @Pattern(regexp = "^[\\w-.]+@(gmail)\\.(com|co\\.vn|co\\.us|etc)$", message = "Invalid Google email")
        String email,

        @NotNull(message = "Phone number can not be null")
        String phoneNumber,

        @NotNull(message = "Password can not be empty")
        @Size(min = 8, max = 30, message = "Password must be between 8 and 20 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must contain at least one lowercase, one uppercase, one number, and one special character")
        @PasswordConstraint(message = "Bro why ur pass is so cringe")
        String password,

        @NotNull(message = "First name is required")
        String firstName,

        @NotNull(message = "Last name ios required")
        String lastName,

        @NotNull(message = "Account is required")
        AccountRequest account,

        @NotEmpty(message = "Card requests must not be empty")
        List<CardRequest> card) {
}
