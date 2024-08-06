package com.backend.bank.api;

import com.backend.bank.dto.request.SignupRequest;
import com.backend.bank.dto.response.SignupResponse;
import com.backend.bank.exception.AccountAlreadyExistsException;
import com.backend.bank.service.intf.SignupService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SignupController {

    private final SignupService signupService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody SignupRequest signupRequest) {
        try {
            SignupResponse response = signupService.signup(signupRequest);
            return ResponseEntity.ok(createSuccessResponse(response));
        } catch (AccountAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("An error occurred"));
        }
    }

    private Map<String, Object> createSuccessResponse(SignupResponse response) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("[timestamp]", new Date());
        responseBody.put("status", HttpStatus.OK.value());
        responseBody.put("message", response.getMessage());
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
