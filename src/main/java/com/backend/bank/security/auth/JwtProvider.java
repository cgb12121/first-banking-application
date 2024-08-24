package com.backend.bank.security.auth;

import com.auth0.jwt.algorithms.Algorithm;
import com.backend.bank.exception.InvalidTokenException;
import com.backend.bank.exception.TokenExpiredException;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.AeadAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings("unused")
@Configuration
public class JwtProvider {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.issuer}")
    private String issuer;

    private final SecureRandom secureRandom = new SecureRandom();

    private static final int ONE_HOUR = 86400000 / 24;

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ONE_HOUR) )
                .subject(userDetails.getUsername())
                .claim("authority", userDetails.getAuthorities())
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        String publicKey = "hello-world";
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .random(secureRandom)
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .notBefore(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ONE_HOUR))
                .subject(userDetails.getUsername())
                .claims(extraClaims)
                .signWith(getSigningKey())
                .encryptWith(getEncryptionKey(), (AeadAlgorithm) Algorithm.HMAC512(getEncryptionKey().toString()))
                .compact();
    }

    private Key getSigningKey() {
        byte[] key = Decoders.BASE64URL.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }

    private SecretKey getEncryptionKey() {
       byte[] key = Decoders.BASE64URL.decode(secretKey);
       return Keys.hmacShaKeyFor(key);
    }

    public <T> T getClaims(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = getAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    public Claims getAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getEncryptionKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("JWT token is expired!");
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("JWT token is invalid!");
        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException("JWT token is unsupported!");
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid JWT token!");
        }
    }

    public static RSAPublicKey getPublicKey(String base64PublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static RSAPrivateKey getPrivateKey(String base64PrivateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public String extractUserName(String token) {
        return getClaims(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        final Date expiration = getClaims(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

}
