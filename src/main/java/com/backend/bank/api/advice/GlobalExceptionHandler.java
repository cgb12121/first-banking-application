package com.backend.bank.api.advice;

import com.backend.bank.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleAccountAlreadyExistsException(AccountAlreadyExistsException e, WebRequest request) {
        return buildErrorResponse(request.getRemoteUser(), e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(BadCredentialsException e, WebRequest request) {
        return buildErrorResponse(request.getRemoteUser(), e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccountNotExistException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotExistException(AccountNotExistException e, WebRequest request) {
        return buildErrorResponse(request.getRemoteUser(), e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccountBannedException.class)
    public ResponseEntity<Map<String, Object>> handleAccountBannedException(AccountBannedException e, WebRequest request) {
        return buildErrorResponse(request.getRemoteUser() ,e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccountInactiveException.class)
    public ResponseEntity<Map<String, Object>> handleAccountInactiveException(AccountInactiveException e, WebRequest request) {
        return buildErrorResponse(request.getRemoteUser(), e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception e, WebRequest request) {
        return buildErrorResponse(request.getRemoteUser(),"An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InputViolationException.class)
    public ResponseEntity<Map<String, Object>> handleInputViolationException(InputViolationException ex, WebRequest request) {
        return buildErrorResponse(request.getRemoteUser(), ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String request, String message, HttpStatus status) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", new Date());
        responseBody.put("request", request);
        responseBody.put("status", status.value());
        responseBody.put("message", message);
        return ResponseEntity.status(status).body(responseBody);
    }
}
