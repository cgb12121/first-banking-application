package com.backend.bank.service.implement;


import com.backend.bank.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.lang.Function;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${PRIVATE_KEY}")
    private String SECRET_KEY;

    private static final int ONE_DAY = 86400000;

    @Override
    @SneakyThrows
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("authority: ", userDetails.getAuthorities())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ONE_DAY))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    @SneakyThrows
    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claims(extraClaims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ONE_DAY) )
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
       byte[] key = Decoders.BASE64URL.decode(SECRET_KEY);
       return Keys.hmacShaKeyFor(key);
    }

    @Override
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    @Override
    public boolean isTokenExpired(String token) {
        final Date expiration = extractClaims(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

}
