package com.backend.bank.service.implement;

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
import com.backend.bank.service.EmailService;
import com.backend.bank.service.SignupService;
import com.backend.bank.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SignupServiceImpl implements SignupService {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    private final AccountRepository accountRepository;

    private final CardRepository cardRepository;

    private final EmailService emailService;

    @Override
    public SignupResponse signup(SignupRequest signupRequest) throws AccountAlreadyExistsException {
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

        Customer customer = new Customer();
        customer.setEmail(signupRequest.getEmail());
        customer.setPhoneNumber(signupRequest.getPhoneNumber());
        customer.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        customer.setFirstName(signupRequest.getFirstName());
        customer.setLastName(signupRequest.getLastName());

        Account account = new Account();
        AccountRequest accountRequest = signupRequest.getAccount();
        account.setAccountNumber(accountRequest.getAccountNumber());
        account.setBalance(accountRequest.getBalance());
        account.setAccountType(accountRequest.getAccountType());
        account.setAccountStatus(accountRequest.getAccountStatus());
        account.setAccountHolder(customer);

        List<Card> cards = signupRequest.getCard().stream().map(cardRequest -> {
            Card card = new Card();
            card.setCardNumber(cardRequest.getCardNumber());
            card.setCardType(cardRequest.getCardType());
            card.setExpiryDate(cardRequest.getExpiryDate());
            card.setCreditLimit(cardRequest.getCreditLimit());
            card.setBalance(cardRequest.getBalance());
            card.setCustomer(customer);
            return card;
        }).collect(Collectors.toList());

        customer.setAccount(account);
        customer.setCards(cards);

        customerRepository.save(customer);

        SignupResponse response = new SignupResponse();
        response.setCustomerId(customer.getId());
        response.setMessage("Signup successful! Please check your email!");

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setReceiver(signupRequest.getEmail());
        emailDetails.setSubject("Signup successful!");
        emailDetails.setBody(EmailUtils.emailAccountCreationSuccess(signupRequest));
        emailService.sendEmail(emailDetails);

        return response;
    }
}
