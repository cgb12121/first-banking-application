package com.backend.bank.api;

import com.backend.bank.dto.request.SignupRequest;
import com.backend.bank.dto.response.SignupResponse;
import com.backend.bank.exception.AccountAlreadyExistsException;
import com.backend.bank.service.intf.SignupService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/auth")
public class SignupController {

    SignupService signupService;

    @PostMapping("/signup")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> signup(
            @RequestBody @Valid SignupRequest signupRequest,
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

        return this.signupService.signup(signupRequest)
                .thenApply(signupResponse -> ResponseEntity.ok(createSuccessResponse(signupResponse)))
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof AccountAlreadyExistsException) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse(ex.getCause().getMessage()));
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("An error occurred"));
                });
    }

    @GetMapping("/verify/{verificationCode}")
    public CompletableFuture<ResponseEntity<String>> verifyAccount(@PathVariable String verificationCode) {
        return this.signupService.verifyUser(verificationCode)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/resend-verify-email")
    public void resendVerifyEmail(@RequestBody SignupRequest signupRequest) {
        this.signupService.resendVerificationEmail(signupRequest);
    }

    private Map<String, Object> createSuccessResponse(SignupResponse response) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", new Date());
        responseBody.put("status", HttpStatus.OK.value());
        responseBody.put("message", response.message());
        return responseBody;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", new Date());
        responseBody.put("status", HttpStatus.CONFLICT.value());
        responseBody.put("message", message);
        return responseBody;
    }
}