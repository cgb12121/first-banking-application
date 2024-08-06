package com.backend.bank.api;

import com.backend.bank.dto.request.LoginRequest;
import com.backend.bank.dto.response.LoginResponse;
import com.backend.bank.exception.AccountNotExistException;
import com.backend.bank.security.auth.JwtAuthenticationFilter;
import com.backend.bank.service.intf.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginController {

    private final AuthenticationService authenticationService;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authenticationService.login(loginRequest);
            return ResponseEntity.ok(createSuccessResponse(response));
        } catch (AccountNotExistException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("An error occurred"));
        }
    }

    private Map<String, Object> createSuccessResponse(LoginResponse response) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", new Date());
        responseBody.put("status", HttpStatus.OK.value());
        responseBody.put("message", response.getMessage());
        responseBody.put("token", response.getToken());
        return responseBody;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", new Date());
        responseBody.put("status", HttpStatus.UNAUTHORIZED.value());
        responseBody.put("message", message);
        return responseBody;
    }

}
