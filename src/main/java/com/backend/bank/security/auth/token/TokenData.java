package com.backend.bank.security.auth.token;

import com.backend.bank.entity.Customer;

import io.jsonwebtoken.Claims;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Builder
@Getter
@Setter
public class TokenData {
    private Customer customer;
    private Claims claims;
    private boolean isValid;
    private List<GrantedAuthority> authorities;
}