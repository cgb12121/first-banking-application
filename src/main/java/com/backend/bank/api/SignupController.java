package com.backend.bank.api;

import com.backend.bank.dto.request.SignupRequest;
import com.backend.bank.dto.response.SignupResponse;
import com.backend.bank.exception.AccountAlreadyExistsException;
import com.backend.bank.exception.InvalidVerifyLink;
import com.backend.bank.service.intf.SignupService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SignupController {

    private final SignupService signupService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody SignupRequest signupRequest) {
        try {
            CompletableFuture<SignupResponse> response = signupService.signup(signupRequest);
            return ResponseEntity.ok(createSuccessResponse(response));
        } catch (AccountAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("An error occurred"));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam("code") String verificationCode) throws InvalidVerifyLink {
        CompletableFuture<String> result = signupService.verifyUser(verificationCode);
        return ResponseEntity.ok(result.join());
    }

    @GetMapping("/resend-verify-email")
    public void resendVerifyEmail(@RequestBody SignupRequest signupRequest) {
        signupService.resendVerificationEmail(signupRequest);
    }

    private Map<String, Object> createSuccessResponse(CompletableFuture<SignupResponse> response) throws ExecutionException, InterruptedException {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("[timestamp]", new Date());
        responseBody.put("status", HttpStatus.OK.value());
        responseBody.put("message", response.get().getMessage());
        return responseBody;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("[timestamp]", new Date());
        responseBody.put("status", HttpStatus.CONFLICT.value());
        responseBody.put("message", message);
        return responseBody;
    }

}
