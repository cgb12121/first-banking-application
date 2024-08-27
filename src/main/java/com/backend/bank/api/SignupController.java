package com.backend.bank.api;

import com.backend.bank.dto.request.SignupRequest;
import com.backend.bank.dto.response.SignupResponse;
import com.backend.bank.exception.AccountAlreadyExistsException;
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
                .thenApply(signupResponse -> {
                    log.info(signupResponse);
                    return ResponseEntity.ok(createSuccessResponse(signupResponse));
                })
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof AccountAlreadyExistsException) {
                        ResponseEntity<Map<String, Object>> errors = ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse(ex.getCause().getMessage(), HttpStatus.CONFLICT));
                        log.error(errors);
                        return errors;
                    }
                    ResponseEntity<Map<String, Object>> errors = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("An error occurred: "+ ex.getCause().getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
                    log.error(errors);
                    return errors;
                });
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

    private Map<String, Object> createSuccessResponse(SignupResponse response) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", new Date());
        responseBody.put("status", HttpStatus.OK.value());
        responseBody.put("message", response.message());
        return responseBody;
    }

    private Map<String, Object> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", new Date());
        responseBody.put("status", status.value());
        responseBody.put("message", message);
        return responseBody;
    }
}