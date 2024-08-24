package com.backend.bank.api.beta;

import com.backend.bank.dto.request.LoginRequest;
import com.backend.bank.dto.response.LoginResponse;
import com.backend.bank.entity.Customer;
import com.backend.bank.security.auth.token.JwtService;
import com.backend.bank.security.auth.token.TokenType;
import com.backend.bank.service.beta.LoginServiceUsingJwtService;
import com.backend.bank.service.intf.CustomerService;

import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/auth")
public class LoginUsingJwtServiceController {

    LoginServiceUsingJwtService loginServiceUsingJwtService;

    AuthenticationManager authenticationManager;

    CustomerService customerService;

    JwtService jwtService;

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> login(
            @RequestBody @Valid LoginRequest loginRequest,
            BindingResult bindingResult,
            HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult
                    .getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", HttpStatus.BAD_REQUEST.value());
            responseBody.put("errors", errors);

            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(responseBody));
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.identifier(), loginRequest.password())
        );
        if (!authentication.isAuthenticated()) {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponse("Unauthorized access")));
        }

        Customer customer = customerService.getCustomerById(Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName()));

        jwtService.addCookie(response, customer, TokenType.ACCESS);

        loginServiceUsingJwtService.login(loginRequest);
        return this.loginServiceUsingJwtService.login(loginRequest)
                .thenApply(loginResponse -> ResponseEntity.ok(createSuccessResponse(loginResponse)))
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause();
                    return switch (cause.getClass().getSimpleName()) {
                        case "AccountNotExistException" ->
                                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponse("Account does not exist."));
                        case "AccountBannedException" ->
                                ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorResponse("Account is banned."));
                        case "AccountInactiveException" ->
                                ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorResponse("Account is inactive."));
                        case "BadCredentialsException" ->
                                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponse("Invalid credentials."));
                        default ->
                                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("An error occurred"));
                    };
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