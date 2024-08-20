package com.backend.bank.utils;


import com.backend.bank.utils.annotation.PasswordConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordConstraint, String> {

    private int minLength;

    private int maxLength;

    private boolean requireUppercase;

    private boolean requireLowercase;

    private boolean requireDigit;

    private boolean requireSpecialChar;

    @Override
    public void initialize(PasswordConstraint constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.maxLength = constraintAnnotation.maxLength();
        this.requireUppercase = constraintAnnotation.requireUppercase();
        this.requireLowercase = constraintAnnotation.requireLowercase();
        this.requireDigit = constraintAnnotation.requireDigit();
        this.requireSpecialChar = constraintAnnotation.requireSpecialChar();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        boolean isValidLength = password.length() > minLength && password.length() < maxLength;
        boolean hasLowerCase = requireLowercase && password.matches(".*[a-z].*");
        boolean hasUpperCase = requireUppercase && password.matches(".*[A-Z].*");
        boolean hasDigit = requireDigit && password.matches(".*[0-9].*");
        boolean hasSpecialChar = requireSpecialChar && password.matches(".*[@$!%*?&].*");

        return isValidLength && hasLowerCase && hasUpperCase && hasDigit && hasSpecialChar;
    }
}

