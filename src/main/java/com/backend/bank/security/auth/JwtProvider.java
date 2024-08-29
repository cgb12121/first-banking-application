package com.backend.bank.security.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Log4j2
@Configuration
public class JwtProvider {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.issuer}")
    private String issuer;

    private final int FIVE_MINUTES = 5 * 60 * 1000;

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .header()
                .add(getHeaders("access-token"))
                .and()
                .issuer(issuer)
                .subject(userDetails.getUsername())
                .claim("authority", userDetails.getAuthorities())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + FIVE_MINUTES))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .header()
                .add(getHeaders("refresh-token"))
                .and()
                .issuer(issuer)
                .subject(userDetails.getUsername())
                .claim("authority", userDetails.getAuthorities())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + (FIVE_MINUTES * 4) ))
                .signWith(getSigningKey())
                .compact();
    }

    public Map<String, Object> getHeaders(String type) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("type", type);
         return headers;
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private SecretKey getSigningKey() {
        byte[] key = Decoders.BASE64URL.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return username.equals(userDetails.getUsername()) &&
                !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        final Date expiration = extractClaims(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}
