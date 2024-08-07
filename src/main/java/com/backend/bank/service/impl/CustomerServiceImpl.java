package com.backend.bank.service.impl;

import com.backend.bank.dto.EmailDetails;
import com.backend.bank.dto.request.ChangeEmailRequest;
import com.backend.bank.dto.request.RegisterNewCardRequest;
import com.backend.bank.dto.request.ChangePasswordRequest;
import com.backend.bank.dto.response.ChangeEmailResponse;
import com.backend.bank.dto.response.RegisterNewCardResponse;
import com.backend.bank.dto.response.ChangePasswordResponse;
import com.backend.bank.entity.Account;
import com.backend.bank.entity.Customer;
import com.backend.bank.repository.AccountRepository;
import com.backend.bank.repository.CustomerRepository;
import com.backend.bank.service.intf.CustomerService;

import com.backend.bank.service.intf.EmailService;
import com.backend.bank.utils.EmailUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService, UserDetailsService {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    private final AccountRepository accountRepository;

    private final EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> customer = customerRepository.findByEmail(username)
                .or(() -> customerRepository.findByPhoneNumber(username))
                .or(() -> customerRepository.findByAccount_AccountNumber(username));

        return customer.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {
        Account accountRequest = accountRepository.findByAccountHolder_Email(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
        if (!passwordEncoder.matches(request.getOldPassword(), accountRequest.getAccountHolder().getPassword())) {
            throw new BadCredentialsException("Wrong password");
        }

        accountRequest.getAccountHolder().setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(accountRequest);

        sendChangedPasswordSuccessEmail(request, new Date());

        return new ChangePasswordResponse("Changed password successfully!");
    }

    public ChangeEmailResponse changeEmail(ChangeEmailRequest request) {
        Account account = accountRepository.findByAccountHolder_Email(request.getOldEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        return new ChangeEmailResponse();
    }

    public void changePhoneNumber(String newPhoneNumber) {

    }

    public RegisterNewCardResponse registerNewCard(RegisterNewCardRequest registerNewCardRequest) {
        return null;
    }

    private void sendChangedPasswordSuccessEmail(ChangePasswordRequest changePasswordRequest, Date changedPasswordDate) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setReceiver(changePasswordRequest.getEmail());
        emailDetails.setSubject("Signup successful!");
        emailDetails.setBody(EmailUtils.sendEmailOnChangePassword(changePasswordRequest, changedPasswordDate));
        emailService.sendEmail(emailDetails);
    }
}