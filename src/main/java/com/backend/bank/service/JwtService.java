package com.backend.bank.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.lang.Function;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtService {
    String generateToken(UserDetails userDetails);

    String generateRefreshToken(Map<String, Object> claims, UserDetails userDetails);

    <T> T extractClaims(String token, Function<Claims, T> claimsResolvers);

    Claims extractAllClaims(String token);

    String extractUserName(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    boolean isTokenExpired(String token);
}
