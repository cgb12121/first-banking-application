package com.backend.bank.service.impl;

import com.backend.bank.entity.enums.AccountStatus;
import com.backend.bank.exception.*;
import com.backend.bank.dto.request.LoginRequest;
import com.backend.bank.dto.response.LoginResponse;
import com.backend.bank.entity.Customer;
import com.backend.bank.repository.CustomerRepository;
import com.backend.bank.security.auth.JwtProvider;
import com.backend.bank.service.intf.LoginService;
import com.backend.bank.utils.RequestValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    private final RequestValidator<LoginRequest> loginRequestRequestValidator;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    @Override
    @Async(value = "userTaskExecutor")
    public CompletableFuture<LoginResponse> login(LoginRequest loginRequest) {
        Set<String> violations = loginRequestRequestValidator.validate(loginRequest);
        if (!violations.isEmpty()) {
            throw new InputViolationException(String.join("\n", violations));
        }

        String email = loginRequest.email();
        String password = loginRequest.password();

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotExistException("Customer not found: " + email));

        boolean isCorrectPassword = passwordEncoder.matches(password, customer.getPassword());
        boolean isAccountInactive = customer.getAccount().getAccountStatus().equals(AccountStatus.INACTIVE);
        boolean isAccountBanned = customer.getAccount().getAccountStatus().equals(AccountStatus.BANNED);

        if (!isCorrectPassword) {
            throw new UnauthorizedAccessException("Invalid password");
        }
        if (isAccountInactive) {
            throw new AccountInactiveException("Your account is not active right now. Please contact us to active your account again.");
        }
        if (isAccountBanned) {
            throw new AccountBannedException("You are banned from using our services!");
        }

//        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password, userDetails.getAuthorities());
//        Authentication authResult = authenticationManager.authenticate(authentication);
//        SecurityContextHolder.getContext().setAuthentication(authResult);

        String token = jwtProvider.generateToken(customer);
        String refreshToken = jwtProvider.generateRefreshToken(null,customer);

        return CompletableFuture.completedFuture(new LoginResponse(
                Collections.singletonList("Login successful"), token, refreshToken)
        );
    }
}
