package com.backend.bank.service.impl;

import com.backend.bank.dto.EmailDetails;
import com.backend.bank.dto.request.AccountRequest;
import com.backend.bank.dto.request.CardRequest;
import com.backend.bank.dto.request.SignupRequest;
import com.backend.bank.dto.response.SignupResponse;
import com.backend.bank.entity.Account;
import com.backend.bank.entity.Card;
import com.backend.bank.entity.Customer;
import com.backend.bank.entity.Verify;
import com.backend.bank.entity.constant.AccountStatus;
import com.backend.bank.entity.constant.AccountType;
import com.backend.bank.exception.AccountAlreadyExistsException;
import com.backend.bank.exception.IllegalAccountTypeException;
import com.backend.bank.exception.InputViolationException;
import com.backend.bank.exception.InvalidVerifyLinkException;
import com.backend.bank.repository.AccountRepository;
import com.backend.bank.repository.CardRepository;
import com.backend.bank.repository.CustomerRepository;
import com.backend.bank.repository.VerifyRepository;
import com.backend.bank.service.intf.NotificationService;
import com.backend.bank.service.intf.SignupService;
import com.backend.bank.utils.EmailUtils;
import com.backend.bank.utils.RequestValidator;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.RandomStringUtils;

import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SignupServiceImpl implements SignupService {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    private final AccountRepository accountRepository;

    private final CardRepository cardRepository;

    private final NotificationService notificationService;

    private final VerifyRepository verifyRepository;

    private final RequestValidator<SignupRequest> signupRequestValidator;

    private final RequestValidator<AccountRequest> accountRequestValidator;

    private final RequestValidator<CardRequest> cardRequestValidator;

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            noRollbackFor = MailException.class
    )
    @Async(value = "userTaskExecutor")
    public CompletableFuture<SignupResponse> signup(SignupRequest signupRequest)
            throws AccountAlreadyExistsException, InputViolationException {

        Set<String> violations = signupRequestValidator.validate(signupRequest);
        if (!violations.isEmpty()) {
            throw new InputViolationException(String.join("\n", violations));
        }

        checkForExistingAccounts(signupRequest);

        Customer customer = createCustomer(signupRequest);
        Account account = createAccount(signupRequest.account(), customer);
        List<Card> cards = createCards(signupRequest.card(), customer);

        customer.setAccount(account);
        customer.setCards(cards);

        AccountType accountType = account.getAccountType();
        BigDecimal interest;
        switch (accountType) {
            case REGULAR -> interest = BigDecimal.valueOf(3.0);
            case VIP -> interest = BigDecimal.valueOf(5.0);
            case BANK_STAFF -> interest = BigDecimal.valueOf(7.0);
            case ENTERPRISE -> interest = BigDecimal.valueOf(10.0);
            case null, default -> throw new IllegalAccountTypeException("Invalid account type");
        }
        customer.getAccount().setInterest(interest);
        customer.getAccount().setBalance(BigDecimal.ZERO);

        customerRepository.save(customer);

        sendSignupSuccessEmail(signupRequest);
        sendVerificationEmail(signupRequest);

        return CompletableFuture.completedFuture(createSignupResponse(customer));
    }

    @Override
    public void resendVerificationEmail(SignupRequest signupRequest) {
        sendVerificationEmail(signupRequest);
    }

    @Override
    @Async(value = "userTaskExecutor")
    public CompletableFuture<String> verifyUser(String httpRequest) throws InvalidVerifyLinkException {

        Verify userVerify = verifyRepository.findByVerifyLink(httpRequest)
                .orElseThrow(() -> new InvalidVerifyLinkException("Invalid verify request: " + httpRequest));

        boolean isExpired = userVerify.getCreateDate().isAfter(userVerify.getExpiryDate());
        if (isExpired) {
            throw new InvalidVerifyLinkException("Verify link expired: " + httpRequest);
        }

        Customer customer = userVerify.getCustomer();
        customer.getAccount().setAccountStatus(AccountStatus.ACTIVE);
        verifyRepository.delete(userVerify);

        return CompletableFuture.completedFuture("Verified successfully");
    }

    private void checkForExistingAccounts(SignupRequest signupRequest) throws AccountAlreadyExistsException {
        boolean isEmailUsed = customerRepository.existsByEmail(signupRequest.email());
        boolean isPhoneNumberUsed = customerRepository.existsByPhoneNumber(signupRequest.phoneNumber());
        boolean isAccountNumberUsed = accountRepository.existsByAccountNumber(signupRequest.account().accountNumber());

        if (isEmailUsed) {
            throw new AccountAlreadyExistsException("Email already exists: " + signupRequest.email());
        }
        if (isPhoneNumberUsed) {
            throw new AccountAlreadyExistsException("Phone number already exists: " + signupRequest.phoneNumber());
        }
        if (isAccountNumberUsed) {
            throw new AccountAlreadyExistsException("Account number already exists: " + signupRequest.account().accountNumber());
        }

        for (CardRequest cardRequest : signupRequest.card()) {
            if (cardRepository.existsByCardNumber(cardRequest.cardNumber())) {
                throw new AccountAlreadyExistsException("Card number: " + cardRequest.cardNumber() + " already exists");
            }
        }
    }

    private Customer createCustomer(SignupRequest signupRequest) {
        Customer customer = new Customer();
        customer.setEmail(signupRequest.email());
        customer.setPhoneNumber(signupRequest.phoneNumber());
        customer.setPassword(passwordEncoder.encode(signupRequest.password()));
        customer.setFirstName(signupRequest.firstName());
        customer.setLastName(signupRequest.lastName());
        return customer;
    }

    private Account createAccount(AccountRequest accountRequest, Customer customer) {

        Set<String> violations = accountRequestValidator.validate(accountRequest);
        if (!violations.isEmpty()) {
            throw new InputViolationException(String.join("\n", violations));
        }

        Account account = new Account();
        account.setAccountNumber(accountRequest.accountNumber());
        account.setBalance(BigDecimal.valueOf(0));
        account.setAccountType(accountRequest.accountType());
        account.setAccountStatus(AccountStatus.INACTIVE);
        account.setAccountHolder(customer);
        return account;
    }

    private List<Card> createCards(List<CardRequest> cardRequests, Customer customer) {

        Set<String> violations =  new HashSet<>();
        for (CardRequest cardRequest : cardRequests) {
            violations.addAll(cardRequestValidator.validate(cardRequest));
        }
        if (!violations.isEmpty()) {
            throw new InputViolationException(String.join("\n", violations));
        }

        BigDecimal finalCreditLimit = getFinalCreditLimit(customer);
        return cardRequests.stream().map(cardRequest -> {
            Card card = new Card();
            card.setCardNumber(cardRequest.cardNumber());
            card.setCardType(cardRequest.cardType());
            card.setExpiryDate(cardRequest.expiryDate());
            card.setCreditLimit(finalCreditLimit);
            card.setBalance(BigDecimal.valueOf(0));
            card.setCustomer(customer);
            return card;
        }).collect(Collectors.toList());
    }

    private static BigDecimal getFinalCreditLimit(Customer customer) {
        BigDecimal creditLimit;
        AccountType accountType = customer.getAccount().getAccountType();
        switch (accountType) {
            case REGULAR, BANK_STAFF -> creditLimit = BigDecimal.valueOf(1000000); //1M
            case VIP -> creditLimit = BigDecimal.valueOf(10000000); //10M
            case ENTERPRISE -> creditLimit = BigDecimal.valueOf(1000000000); // 1T
            case null, default -> throw new IllegalAccountTypeException("Invalid account type");
        }

        return creditLimit;
    }

    private void sendSignupSuccessEmail(SignupRequest signupRequest) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setReceiver(signupRequest.email());
        emailDetails.setSubject("Signup successful!");
        emailDetails.setBody(EmailUtils.emailAccountCreationSuccess(signupRequest, new Date()));

        notificationService.sendEmailToCustomer(emailDetails);
    }

    private void sendVerificationEmail(SignupRequest signupRequest) {
        Customer customer = customerRepository.findByEmail(signupRequest.email()).orElseThrow();
        String verificationLink = generateVerificationCode(customer);

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setReceiver(customer.getEmail());
        emailDetails.setSubject("Verification Email");
        emailDetails.setBody("Verification Link: " + verificationLink);

        notificationService.sendEmailToCustomer(emailDetails);
    }

    private SignupResponse createSignupResponse(Customer customer) {
        return new SignupResponse(
                customer.getId(),
                "Signup successful! Please check your email!"
        );
    }

    private String generateVerificationCode(Customer customer) {
        String siteVerifyURL = "localhost:8080/auth/verify/";
        String encodedUserName = URLEncoder.encode(customer.getFirstName(), StandardCharsets.UTF_8);
        String userVerifyPath = RandomStringUtils.randomAlphanumeric(30) + "+" + encodedUserName;
        String verifyLink = siteVerifyURL + userVerifyPath;

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expiryDate = currentTime.plusMinutes(15);

        Verify verify = new Verify();
        verify.setVerifyLink(verifyLink);
        verify.setCreateDate(currentTime);
        verify.setExpiryDate(expiryDate);
        verify.setCustomer(customer);

        verifyRepository.save(verify);

        return verifyLink;
    }
}
