package com.backend.bank.api;

import com.backend.bank.dto.request.LoginRequest;
import com.backend.bank.dto.request.SignupRequest;
import com.backend.bank.dto.response.LoginResponse;
import com.backend.bank.dto.response.SignupResponse;
import com.backend.bank.service.intf.LoginService;

import com.backend.bank.service.intf.SignupService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@CrossOrigin
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/auth")
public class AuthController {

    LoginService loginService;

    SignupService signupService;

    @PostMapping("/signup")
    public CompletableFuture<ResponseEntity<SignupResponse>> signup(
            @RequestBody @Valid SignupRequest signupRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            SignupResponse errorResponse = new SignupResponse(errors);
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(errorResponse));
        }

        return this.signupService.signup(signupRequest)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/verify/{verificationCode}")
    public CompletableFuture<ResponseEntity<String>> verifyAccount(@PathVariable String verificationCode) {
        return this.signupService.verifyUser(verificationCode)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/resend-verify-email")
    public void resendVerifyEmail(@RequestBody SignupRequest signupRequest) {
        this.signupService.resendVerificationEmail(signupRequest);
    }

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
                .orTimeout(1, TimeUnit.SECONDS);
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

                LoginResponse errorResponse = new LoginResponse(errors);
                return ResponseEntity.badRequest().body(errorResponse);
            }

            return loginService.login(loginRequest)
                    .thenApply(ResponseEntity::ok)
                    .orTimeout(1, TimeUnit.SECONDS)
                    .join();
        });
    }

    @PostMapping("/logout")
    public CompletableFuture<ResponseEntity<String>> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return CompletableFuture.supplyAsync(() -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                new SecurityContextLogoutHandler().logout(request, response, authentication);
                SecurityContextHolder.clearContext();
                return ResponseEntity.ok("Logout successful.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authenticated user found.");
            }
        });
    }


    @PostMapping("/logout-v2")
    public CompletableFuture<ResponseEntity<String>> logoutV2(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        return CompletableFuture.supplyAsync(() -> {
            if (authentication != null && authentication.isAuthenticated()) {
                new SecurityContextLogoutHandler().logout(request, response, authentication);

                try {
                    request.logout();
                } catch (ServletException e) {
                    throw new RuntimeException(e);
                }

                return ResponseEntity.ok("Logout successful.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authenticated user found.");
            }
        });
    }
}
