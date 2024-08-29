package com.backend.bank.api;

import com.backend.bank.dto.request.LoginRequest;
import com.backend.bank.dto.response.LoginResponse;
import com.backend.bank.service.LoginAttemptService;
import com.backend.bank.service.intf.LoginService;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Log4j2
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/auth")
public class LoginController {

    LoginService loginService;

    AuthenticationManager authenticationManager;

    LoginAttemptService loginAttemptService;

    @GetMapping("/login")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> login(
            @RequestBody @Valid LoginRequest loginRequest,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult
                    .getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("status: ", HttpStatus.BAD_REQUEST.value());
            response.put("errors: ", errors);

            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(response));
        }


        return this.loginService.login(loginRequest)
                .thenApply(loginResponse -> {
                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(loginRequest.identifier(), loginRequest.password())
                    );

                    if (!authentication.isAuthenticated()) {
                        loginAttemptService.updateLoginAttempt(loginRequest.identifier());
                        return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(createErrorResponse());
                    }

                    log.info("[Timestamp: {}][Username]{}", Instant.now(), authentication.getName());
                    authentication.getAuthorities().forEach(
                            authority -> log.info("[Timestamp: {}][Role] {}", Instant.now(), authority.getAuthority())
                    );

                    return ResponseEntity.ok(createSuccessResponse(loginResponse));
                });
    }

    private Map<String, Object> createSuccessResponse(LoginResponse response) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", new Date());
        responseBody.put("status", HttpStatus.OK.value());
        responseBody.put("message", response.message());
        responseBody.put("token", response.token());
        responseBody.put("refreshToken", response.refreshToken());
        return responseBody;
    }

    private Map<String, Object> createErrorResponse() {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", new Date());
        responseBody.put("message", "Unauthorized access");
        return responseBody;
    }
}

