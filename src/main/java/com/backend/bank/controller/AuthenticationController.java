package com.backend.bank.controller;

import com.backend.bank.dto.request.LoginRequest;
import com.backend.bank.dto.response.LoginResponse;
import com.backend.bank.exception.AccountNotExistException;
import com.backend.bank.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequestDTO) throws AccountNotExistException {
        LoginResponse response = authenticationService.login(loginRequestDTO);
        return ResponseEntity.ok(response);
    }
}
