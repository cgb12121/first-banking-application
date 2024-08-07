package com.backend.bank.security.auth;

import com.backend.bank.exception.InvalidTokenException;
import com.backend.bank.exception.TokenExpiredException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.lang.Function;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtProviderImpl implements JwtProvider {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.issuer}")
    private String issuer;

    private static final int ONE_DAY = 86400000;

    @Override
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ONE_DAY) )
                .subject(userDetails.getUsername())
                .claim("authority: ", userDetails.getAuthorities())
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ONE_DAY))
                .subject(userDetails.getUsername())
                .claims(extraClaims)
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
       byte[] key = Decoders.BASE64URL.decode(secretKey);
       return Keys.hmacShaKeyFor(key);
    }

    @Override
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolvers) throws TokenExpiredException, InvalidTokenException {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    @Override
    public Claims extractAllClaims(String token) throws TokenExpiredException, InvalidTokenException {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("JWT token is expired!");
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid JWT token!");
        }
    }

    @Override
    public String extractUserName(String token) throws InvalidTokenException, TokenExpiredException {
        return extractClaims(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) throws InvalidTokenException, TokenExpiredException {
        final String username = extractUserName(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    @Override
    public boolean isTokenExpired(String token) throws InvalidTokenException, TokenExpiredException {
        final Date expiration = extractClaims(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

}
