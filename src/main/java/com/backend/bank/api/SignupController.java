package com.backend.bank.api;

import com.backend.bank.dto.request.SignupRequest;
import com.backend.bank.dto.response.SignupResponse;
import com.backend.bank.service.intf.SignupService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class SignupController {

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
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    log.error(ex);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
                });
    }

    @PostMapping("/resend-verify-email")
    public void resendVerifyEmail(@RequestBody SignupRequest signupRequest) {
        this.signupService.resendVerificationEmail(signupRequest);
    }
}