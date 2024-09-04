package com.backend.bank.api;

import com.backend.bank.dto.request.LoginRequest;
import com.backend.bank.dto.response.LoginResponse;
import com.backend.bank.service.intf.LoginService;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/auth")
public class LoginController {

    LoginService loginService;

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<LoginResponse>> login(
            @RequestBody @Valid LoginRequest loginRequest,
            BindingResult bindingResult)  {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            errors.addFirst(Instant.now().toString());

            LoginResponse errorResponse = new LoginResponse(errors);
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(errorResponse));
        }

        return loginService.login(loginRequest)
                .thenApply(ResponseEntity::ok)
                .orTimeout(0, TimeUnit.SECONDS);
    }

    @PostMapping("/login-v2")
    public CompletableFuture<ResponseEntity<LoginResponse>> loginV2(
            @RequestBody @Valid LoginRequest loginRequest,
            BindingResult bindingResult) {

        return CompletableFuture.supplyAsync(() -> {

            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getAllErrors()
                        .stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.toList());

                errors.addFirst(Instant.now().toString());

                LoginResponse errorResponse = new LoginResponse(errors, null, null);
                return ResponseEntity.badRequest().body(errorResponse);
            }

            return loginService.login(loginRequest)
                    .thenApply(ResponseEntity::ok)
                    .orTimeout(10, TimeUnit.SECONDS)
                    .join();
        });
    }
}
