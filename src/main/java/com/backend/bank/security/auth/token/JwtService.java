package com.backend.bank.security.auth.token;

import com.backend.bank.entity.Customer;
import com.backend.bank.service.intf.CustomerService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.function.TriConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.backend.bank.security.auth.token.TokenType.ACCESS;
import static com.backend.bank.security.auth.token.TokenType.REFRESH;
import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    private final CustomerService customerService;

    private final Supplier<SecretKey> key = () -> Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

    private final Function<String, Claims> claimsFunction = token ->
            Jwts.parser()
                    .verifyWith(key.get())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

    private final Function<String, String> subject = token ->
            getClaimsValue(token, Claims::getSubject);

    private final BiFunction<HttpServletRequest, String, Optional<String>> extractToken = (request, cookieName) -> {
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return Stream.of(request.getCookies())
                .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                .map(Cookie::getValue)
                .findAny();
    };

    private final BiFunction<HttpServletRequest, String, Optional<Cookie>> extractCookie = (request, cookieName) -> {
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return Stream.of(request.getCookies())
                .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                .findAny();
    };

    private final Supplier<JwtBuilder> builder = () ->
            Jwts.builder()
                .header().add(Map.of("typ", "JWT"))
                .and()
                .audience().add("GET_ARRAYS_LLC")
                .and()
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(Instant.now()))
                .notBefore(new Date())
                .signWith(key.get(), Jwts.SIG.HS512);

    private <T> T getClaimsValue(String token, Function<Claims, T> claims) {
        return claimsFunction.andThen(claims).apply(token);
    }

    private final BiFunction<Customer, TokenType, String> buildToken = (customer, tokenType) ->
            Objects.equals(tokenType, ACCESS) ? builder.get()
                    .subject(String.valueOf(customer.getId()))
                    .claim("authority", customer.getAuthorities())
                    .claim("role", customer.getRole())
                    .compact() : builder.get()
                    .subject(String.valueOf(customer.getId()))
                    .expiration(Date.from(Instant.now().plusSeconds(60 * 60)))
                    .compact();

    private final TriConsumer<HttpServletResponse, Customer, TokenType> addCookie = (response, user, tokenType) -> {
        switch (tokenType) {
            case ACCESS -> {
                String accessToken = createToken(user, Token::getAccessToken);
                Cookie cookie = new Cookie("access_token", accessToken);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setMaxAge(2 * 60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", "None");
                response.addCookie(cookie);
            }

            case REFRESH -> {
                String refreshToken = createToken(user, Token::getAccessToken);
                Cookie cookie = new Cookie("access_token", refreshToken);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setMaxAge(2 * 60 * 60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", "None");
                response.addCookie(cookie);
            }
        }
    };

    public Function<String, List<GrantedAuthority>> authority = token ->
            commaSeparatedStringToAuthorityList(
                    new StringJoiner("AUTHORITY_")
                            .add(claimsFunction
                                    .apply(token)
                                    .get("AUTHORITIES", String.class)
                            ).add("ROLE_PREFIX" + claimsFunction
                                    .apply(token)
                                    .get("ROLE", String.class))
                    .toString()
            );

    public String createToken(Customer customer, Function<Token, String> tokenFunction) {
        Token token = Token.builder()
                .accessToken(buildToken.apply(customer, ACCESS))
                .refreshToken(buildToken.apply(customer, REFRESH))
                .build();
        return tokenFunction.apply(token);
    }

    public Optional<String> extractToken(HttpServletRequest request, String cookieName) {
        return extractToken.apply(request, cookieName);
    }

    public void addCookie(HttpServletResponse response, Customer customer, TokenType tokenType) {
        addCookie.accept(response, customer, tokenType);
    }

    public <T> T getTokenData(String token, Function<TokenData, T> tokenFunction) {
        Long customerId = Long.valueOf(subject.apply(token));
        Customer customer = customerService.getCustomerById(customerId);

        boolean isValid = Objects.equals(String.valueOf(customer.getId()), claimsFunction.apply(token).getSubject());

        TokenData tokenData = TokenData.builder()
                .isValid(isValid)
                .authorities(authority.apply(token))
                .customer(customer)
                .build();

        return tokenFunction.apply(tokenData);
    }


    public void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        var optionalCookie = extractCookie.apply(request, cookieName);
        if (optionalCookie.isPresent()) {
            var cookie = optionalCookie.get();
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}