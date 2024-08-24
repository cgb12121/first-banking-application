package com.backend.bank.security.auth.token;

import com.backend.bank.entity.Customer;

import lombok.Getter;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

@Getter
public class ApiAuthentication extends AbstractAuthenticationToken {

    private static final String PASSWORD_PROTECTED = "[PASSWORD PROTECTED]";

    private static final String EMAIL_PROTECTED = "[EMAIL PROTECTED]";

    private Customer customer;

    private final String email;

    private final String password;

    private final boolean isAuthenticated;

    private ApiAuthentication(String email, String password) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.email = email;
        this.password = password;
        this.isAuthenticated = false;
    }

    private ApiAuthentication(Customer customer, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.customer = customer;
        this.email = EMAIL_PROTECTED;
        this.password = PASSWORD_PROTECTED;
        this.isAuthenticated = true;
    }

    public static ApiAuthentication unAuthenticated (String email, String password) {
        return new ApiAuthentication(email, password);
    }

    public static ApiAuthentication authenticated(Customer customer, Collection<? extends GrantedAuthority> authorities){
        return new ApiAuthentication(customer, authorities);
    }

    @Override
    public Object getCredentials() {
        return PASSWORD_PROTECTED;
    }

    @Override
    public Object getPrincipal() {
        return this.customer;
    }

}