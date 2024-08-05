package com.backend.bank.security.auth;

import com.backend.bank.exception.InvalidTokenException;
import com.backend.bank.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.lang.Function;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtProvider {
    String generateToken(UserDetails userDetails);

    String generateRefreshToken(Map<String, Object> claims, UserDetails userDetails);

    <T> T extractClaims(String token, Function<Claims, T> claimsResolvers) throws TokenExpiredException, InvalidTokenException;

    Claims extractAllClaims(String token) throws TokenExpiredException, InvalidTokenException;

    String extractUserName(String token) throws TokenExpiredException, InvalidTokenException;

    boolean isTokenValid(String token, UserDetails userDetails) throws TokenExpiredException, InvalidTokenException;

    boolean isTokenExpired(String token) throws TokenExpiredException, InvalidTokenException;
}
