package com.backend.bank.api.beta;

import com.backend.bank.security.auth.token.JwtService;
import com.backend.bank.security.auth.token.TokenData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Function;

@RestController
@RequiredArgsConstructor
@RequestMapping("/protected")
public class ProtectedResourceController {

    private final JwtService jwtService;

    @GetMapping("/")
    public ResponseEntity<String> getProtectedResource(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring("Bearer ".length());

        TokenData tokenData = jwtService.getTokenData(token, Function.identity());

        if (tokenData.isValid()) {
            return ResponseEntity.ok("You have access to this resource.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or user not authorized.");
        }
    }
}