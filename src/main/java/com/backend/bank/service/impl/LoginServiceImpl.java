package com.backend.bank.service.impl;

import com.backend.bank.entity.constant.AccountStatus;
import com.backend.bank.exception.AccountBannedException;
import com.backend.bank.exception.AccountInactiveException;
import com.backend.bank.security.auth.JwtProviderImpl;
import com.backend.bank.dto.request.LoginRequest;
import com.backend.bank.dto.response.LoginResponse;
import com.backend.bank.entity.Customer;
import com.backend.bank.exception.AccountNotExistException;
import com.backend.bank.repository.CustomerRepository;
import com.backend.bank.service.intf.LoginService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtProviderImpl jwtService;

    @Async
    @Override
    @Transactional(rollbackOn = Exception.class, dontRollbackOn = MailException.class)
    public CompletableFuture<LoginResponse> login(LoginRequest loginRequest)
            throws AccountNotExistException, AccountBannedException, AccountInactiveException {
        String identifier = loginRequest.identifier();
        String password = loginRequest.password();

        Optional<Customer> optionalCustomer = findCustomerByIdentifier(identifier);
        if (optionalCustomer.isEmpty()) {
            throw new AccountNotExistException("Customer not found: " + identifier);
        }

        Customer customer = optionalCustomer.get();
        if (!passwordEncoder.matches(password, customer.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        if (customer.getAccount().getAccountStatus().equals(AccountStatus.INACTIVE)) {
            throw new AccountInactiveException("Your account is not active right now. Please contact us to active your account again.");
        }
        if (customer.getAccount().getAccountStatus() == AccountStatus.BANNED) {
            throw new AccountBannedException("You are banned from using our services!");
        }

        String token = jwtService.generateToken(customer);

        return CompletableFuture.completedFuture(new LoginResponse("Login successful", token));
    }

    private Optional<Customer> findCustomerByIdentifier(String identifier) {
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(identifier);
        if (optionalCustomer.isEmpty()) {
            optionalCustomer = customerRepository.findByPhoneNumber(identifier);
        }
        if (optionalCustomer.isEmpty()) {
            optionalCustomer = customerRepository.findByAccount_AccountNumber(identifier);
        }
        return optionalCustomer;
    }
}

