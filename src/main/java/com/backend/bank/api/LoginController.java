package com.backend.bank.api;

import com.backend.bank.dto.request.LoginRequest;
import com.backend.bank.dto.response.LoginResponse;
import com.backend.bank.exception.AccountBannedException;
import com.backend.bank.exception.AccountInactiveException;
import com.backend.bank.exception.AccountNotExistException;
import com.backend.bank.service.intf.LoginService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> login(
            @RequestBody @Valid LoginRequest loginRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("status: ", HttpStatus.BAD_REQUEST.value());
            response.put("errors: ", errors);

            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(response));
        }

        return this.loginService.login(loginRequest)
                .thenApply(loginResponse -> ResponseEntity.ok(createSuccessResponse(loginResponse)))
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause();
                    if (cause instanceof AccountNotExistException) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponse("Account does not exist."));
                    } else if (cause instanceof AccountBannedException) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorResponse("Account is banned."));
                    } else if (cause instanceof AccountInactiveException) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorResponse("Account is inactive."));
                    } else if (cause instanceof BadCredentialsException) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponse("Invalid credentials."));
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("An error occurred"));
                });
    }

    private Map<String, Object> createSuccessResponse(LoginResponse response) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", new Date());
        responseBody.put("status", HttpStatus.OK.value());
        responseBody.put("message", response.message());
        responseBody.put("token", response.token());
        return responseBody;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", new Date());
        responseBody.put("message", message);
        return responseBody;
    }
}

