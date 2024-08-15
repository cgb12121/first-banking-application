package com.backend.bank.dto.request;

import com.backend.bank.utils.annotation.PasswordConstraint;
import com.backend.bank.utils.annotation.PasswordMatch;
import jakarta.validation.constraints.*;

@PasswordMatch(password = "newPassword", confirmPassword = "confirmNewPassword")
public record ChangePasswordRequest(

        @NotNull(message = "You must provide your new email address")
        @Email(message = "Please enter a valid email address")
        @NotBlank(message = "This field can not be blank or contain any white space")
        @Size(min = 5, max = 100, message = "Email length must be between 5 and 100 characters")
        @Pattern(regexp = "^[\\w-.]+@(gmail)\\.(com|co\\.vn|co\\.us|etc)$", message = "Invalid Google email")
        String email,

        @NotNull(message = "Password can not be empty")
        @Size(min = 8, max = 30, message = "Password must be between 8 and 20 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must contain at least one lowercase, one uppercase, one number, and one special character")
        @PasswordConstraint
        String oldPassword,

        @NotNull(message = "Password can not be empty")
        @Size(min = 8, max = 30, message = "Password must be between 8 and 20 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must contain at least one lowercase, one uppercase, one number, and one special character")
        @PasswordConstraint
        String newPassword,

        @NotNull(message = "Please confirm your new password")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must contain at least one lowercase, one uppercase, one number, and one special character")
        @PasswordConstraint
        String confirmNewPassword) {
}
