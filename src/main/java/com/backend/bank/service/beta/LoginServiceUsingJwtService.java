package com.backend.bank.service.beta;

import com.backend.bank.dto.request.LoginRequest;
import com.backend.bank.dto.response.LoginResponse;
import com.backend.bank.entity.Customer;
import com.backend.bank.entity.constant.AccountStatus;
import com.backend.bank.exception.AccountBannedException;
import com.backend.bank.exception.AccountInactiveException;
import com.backend.bank.exception.AccountNotExistException;
import com.backend.bank.exception.InputViolationException;
import com.backend.bank.repository.CustomerRepository;
import com.backend.bank.security.auth.token.JwtService;
import com.backend.bank.security.auth.token.Token;
import com.backend.bank.security.auth.token.TokenType;
import com.backend.bank.utils.RequestValidator;
import com.google.common.annotations.Beta;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Beta
@Service
@RequiredArgsConstructor
public class LoginServiceUsingJwtService {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final RequestValidator<LoginRequest> loginRequestRequestValidator;

    @Async
    public CompletableFuture<LoginResponse> login(LoginRequest loginRequest)
            throws AccountNotExistException, AccountBannedException, AccountInactiveException, InputViolationException {
        Set<String> violations = loginRequestRequestValidator.validate(loginRequest);
        if (!violations.isEmpty()) {
            throw new InputViolationException(String.join("\n", violations));
        }

        String identifier = loginRequest.identifier();
        String password = loginRequest.password();

        Customer customer = findCustomerByIdentifier(identifier)
                .orElseThrow(() -> new AccountNotExistException("Customer not found: " + identifier));

        boolean isCorrectPassword = passwordEncoder.matches(password, customer.getPassword());
        boolean isAccountInactive = customer.getAccount().getAccountStatus().equals(AccountStatus.INACTIVE);
        boolean isAccountBanned = customer.getAccount().getAccountStatus().equals(AccountStatus.BANNED);

        if (!isCorrectPassword) {
            throw new BadCredentialsException("Invalid password");
        }
        if (isAccountInactive) {
            throw new AccountInactiveException("Your account is not active right now. Please contact us to active your account again.");
        }
        if (isAccountBanned) {
            throw new AccountBannedException("You are banned from using our services!");
        }

        String token = jwtService.createToken(customer, Token::getAccessToken);

        return CompletableFuture.completedFuture(new LoginResponse("Login successful", token));
    }

    private Optional<Customer> findCustomerByIdentifier(String identifier) {
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(identifier);
        boolean isUserNotExist = optionalCustomer.isEmpty();

        if (isUserNotExist) {
            optionalCustomer = customerRepository.findByPhoneNumber(identifier);
        }
        if (isUserNotExist) {
            optionalCustomer = customerRepository.findByAccount_AccountNumber(identifier);
        }
        return optionalCustomer;
    }
}