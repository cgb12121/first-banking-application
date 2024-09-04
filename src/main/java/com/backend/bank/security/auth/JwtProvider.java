package com.backend.bank.security.auth;

import com.backend.bank.exception.InvalidSecretTokenException;
import com.backend.bank.exception.TokenExpiredException;
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

    private final Long ONE_HOUR = 60 * 60 * 1000L;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("type", "token");
        getInfo(userDetails, "access-token");
        return Jwts.builder()
                .header()
                .add(headers)
                .and()
                .issuer(issuer)
                .subject(userDetails.getUsername())
                .claim("authority", userDetails.getAuthorities())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ONE_HOUR))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("type", "refresh-token");
        getInfo(userDetails, "refresh-token");
        return Jwts.builder()
                .header()
                .add(headers)
                .and()
                .issuer(issuer)
                .subject(userDetails.getUsername())
                .claims(extraClaims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + (ONE_HOUR * 8)) )
                .signWith(getSigningKey())
                .compact();
    }

    private static void getInfo(UserDetails userDetails, String tokenType) {
        log.info("------------Generate {}--------------", tokenType);
        log.info("userDetails: {}", userDetails);
        log.info("username: {}", userDetails.getUsername());
        log.info("authorities: {}", userDetails.getAuthorities());
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
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Token Expired: " + e.getMessage());
        } catch (JwtException e) {
            log.error("Invalid secret key or tampered token: {}", e.getMessage(), e);
            throw new InvalidSecretTokenException("Invalid Token");
        }
    }

    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return username.equals(userDetails.getUsername()) &&
                !isTokenExpired(token) &&
                isValidSecretKey(token);
    }

    public boolean isTokenExpired(String token) {
        final Date expiration = extractClaims(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    public boolean isValidSecretKey(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            log.error("Invalid signature: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw e;
        }
    }
}
