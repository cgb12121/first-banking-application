package com.backend.bank.service.impl;

import com.backend.bank.dto.EmailDetails;
import com.backend.bank.dto.request.AccountRequest;
import com.backend.bank.dto.request.CardRequest;
import com.backend.bank.dto.request.SignupRequest;
import com.backend.bank.dto.response.SignupResponse;
import com.backend.bank.entity.Account;
import com.backend.bank.entity.Card;
import com.backend.bank.entity.Customer;
import com.backend.bank.exception.AccountAlreadyExistsException;
import com.backend.bank.repository.AccountRepository;
import com.backend.bank.repository.CardRepository;
import com.backend.bank.repository.CustomerRepository;
import com.backend.bank.service.intf.EmailService;
import com.backend.bank.service.intf.SignupService;
import com.backend.bank.utils.EmailUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SignupServiceImpl implements SignupService {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    private final AccountRepository accountRepository;

    private final CardRepository cardRepository;

    private final EmailService emailService;

    @Async
    @Override
    @Transactional(rollbackOn = Exception.class, dontRollbackOn = MailException.class)
    public CompletableFuture<SignupResponse> signup(SignupRequest signupRequest) throws AccountAlreadyExistsException {
        checkForExistingAccounts(signupRequest);

        Customer customer = createCustomer(signupRequest);
        Account account = createAccount(signupRequest.getAccount(), customer);
        List<Card> cards = createCards(signupRequest.getCard(), customer);

        customer.setAccount(account);
        customer.setCards(cards);

        customerRepository.save(customer);

        sendSignupSuccessEmail(signupRequest);

        return CompletableFuture.completedFuture(createSignupResponse(customer));
    }

    private void checkForExistingAccounts(SignupRequest signupRequest) throws AccountAlreadyExistsException {
        if (customerRepository.existsByEmail(signupRequest.getEmail())) {
            throw new AccountAlreadyExistsException("Email already exists: " + signupRequest.getEmail());
        }

        if (customerRepository.existsByPhoneNumber(signupRequest.getPhoneNumber())) {
            throw new AccountAlreadyExistsException("Phone number already exists: " + signupRequest.getPhoneNumber());
        }

        if (accountRepository.existsByAccountNumber(signupRequest.getAccount().getAccountNumber())) {
            throw new AccountAlreadyExistsException("Account number already exists: " + signupRequest.getAccount().getAccountNumber());
        }

        for (CardRequest cardRequest : signupRequest.getCard()) {
            if (cardRepository.existsByCardNumber(cardRequest.getCardNumber())) {
                throw new AccountAlreadyExistsException("Card number: " + cardRequest.getCardNumber() + " already exists");
            }
        }
    }

    private Customer createCustomer(SignupRequest signupRequest) {
        Customer customer = new Customer();
        customer.setEmail(signupRequest.getEmail());
        customer.setPhoneNumber(signupRequest.getPhoneNumber());
        customer.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        customer.setFirstName(signupRequest.getFirstName());
        customer.setLastName(signupRequest.getLastName());
        return customer;
    }

    private Account createAccount(AccountRequest accountRequest, Customer customer) {
        Account account = new Account();
        account.setAccountNumber(accountRequest.getAccountNumber());
        account.setBalance(accountRequest.getBalance());
        account.setAccountType(accountRequest.getAccountType());
        account.setAccountStatus(accountRequest.getAccountStatus());
        account.setAccountHolder(customer);
        return account;
    }

    private List<Card> createCards(List<CardRequest> cardRequests, Customer customer) {
        return cardRequests.stream().map(cardRequest -> {
            Card card = new Card();
            card.setCardNumber(cardRequest.getCardNumber());
            card.setCardType(cardRequest.getCardType());
            card.setExpiryDate(cardRequest.getExpiryDate());
            card.setCreditLimit(cardRequest.getCreditLimit());
            card.setBalance(cardRequest.getBalance());
            card.setCustomer(customer);
            return card;
        }).collect(Collectors.toList());
    }

    private void sendSignupSuccessEmail(SignupRequest signupRequest) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setReceiver(signupRequest.getEmail());
        emailDetails.setSubject("Signup successful!");
        emailDetails.setBody(EmailUtils.emailAccountCreationSuccess(signupRequest, new Date()));

        emailService.sendEmailToCustomer(emailDetails);
    }

    private void sendVerificationEmail(SignupRequest signupRequest) {
        Customer customer = customerRepository.findByEmail(signupRequest.getEmail()).orElseThrow();
        String verificationLink = generateVerificationCode(customer);

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setReceiver(customer.getEmail());
        emailDetails.setSubject("Verification Email");
        emailDetails.setBody("Verification Link: " + verificationLink);

        emailService.sendEmailToCustomer(emailDetails);
    }

    private SignupResponse createSignupResponse(Customer customer) {
        SignupResponse response = new SignupResponse();
        response.setCustomerId(customer.getId());
        response.setMessage("Signup successful! Please check your email!");
        return response;
    }

    private String generateVerificationCode(Customer Customer) {
        String siteVerifyURL = "localhost:8080/auth/verify=?";
        // TODO: save to verification data base
        // TODO: (include RandomStringUtils.randomAlphanumeric(30), issued date, expire date (15 min later))
        String encodeUserName = passwordEncoder.encode(Customer.getFirstName());
        return siteVerifyURL + RandomStringUtils.randomAlphanumeric(30) + encodeUserName;
    }
}
