package com.backend.bank.utils;

import com.backend.bank.utils.annotation.PasswordMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;

@Log4j2
public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    private String passwordFieldName;

    private String confirmPasswordFieldName;

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        this.passwordFieldName = constraintAnnotation.password();
        this.confirmPasswordFieldName = constraintAnnotation.confirmPassword();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        try {
            Field passwordField = value.getClass().getDeclaredField(passwordFieldName);
            Field confirmPasswordField = value.getClass().getDeclaredField(confirmPasswordFieldName);

            passwordField.setAccessible(true);
            confirmPasswordField.setAccessible(true);

            Object password = passwordField.get(value);
            Object confirmPassword = confirmPasswordField.get(value);

            if (password == null || confirmPassword == null) {
                return false;
            }

            return password.equals(confirmPassword);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error(e);
            return false;
        }
    }
}
