package com.backend.bank.dto.request;

import jakarta.validation.constraints.*;

public record ChangeEmailRequest(

        @NotNull(message = "You must provide your old email address")
        @Email(message = "Please enter a valid email address")
        @NotBlank(message = "This field can not be blank or contain any white space")
        @Size(min = 5, max = 100, message = "Email length must be between 5 and 100 characters")
        @Pattern(regexp = "^[\\w-.]+@(gmail)\\.(com|co\\.vn|co\\.us|etc)$", message = "Invalid Google email")
        String oldEmail,

        @NotNull(message = "You must provide your new email address")
        @Email(message = "Please enter a valid email address")
        @NotBlank(message = "This field can not be blank or contain any white space")
        @Size(min = 5, max = 100, message = "Email length must be between 5 and 100 characters")
        @Pattern(regexp = "^[\\w-.]+@(gmail)\\.(com|co\\.vn|co\\.us|etc)$", message = "Invalid Google email")
        String newEmail,

        @NotNull(message = "Please enter your password")
        @NotBlank(message = "This field can not be blank or contain any white space")
        String confirmPassword) {
}
