package com.backend.bank.service.implement;

import com.backend.bank.auth.JwtProviderImpl;
import com.backend.bank.dto.request.LoginRequest;
import com.backend.bank.dto.response.LoginResponse;
import com.backend.bank.entity.Customer;
import com.backend.bank.exception.AccountNotExistException;
import com.backend.bank.repository.CustomerRepository;
import com.backend.bank.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtProviderImpl jwtService;

    @Override
    public LoginResponse login(LoginRequest loginRequest) throws AccountNotExistException {
        String identifier = loginRequest.getIdentifier();
        String password = loginRequest.getPassword();

        Optional<Customer> optionalCustomer = findCustomerByIdentifier(identifier);
        if (optionalCustomer.isEmpty()) {
            throw new AccountNotExistException("Customer not found: " + identifier);
        }

        Customer customer = optionalCustomer.get();
        if (!passwordEncoder.matches(password, customer.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        String token = jwtService.generateToken(customer);
        return new LoginResponse("Login successful", token);
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

