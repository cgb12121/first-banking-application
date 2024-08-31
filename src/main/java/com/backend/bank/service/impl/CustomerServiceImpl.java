package com.backend.bank.service.impl;

import com.backend.bank.dto.EmailDetails;
import com.backend.bank.dto.request.ChangeEmailRequest;
import com.backend.bank.dto.request.ChangePhoneNumberRequest;
import com.backend.bank.dto.request.RegisterNewCardRequest;
import com.backend.bank.dto.request.ChangePasswordRequest;
import com.backend.bank.dto.response.ChangeEmailResponse;
import com.backend.bank.dto.response.ChangePhoneNumberResponse;
import com.backend.bank.dto.response.RegisterNewCardResponse;
import com.backend.bank.dto.response.ChangePasswordResponse;
import com.backend.bank.entity.Account;
import com.backend.bank.entity.Customer;
import com.backend.bank.entity.EmailChangeToken;
import com.backend.bank.entity.PhoneChangeToken;
import com.backend.bank.exception.AccountNotExistException;
import com.backend.bank.exception.InputViolationException;
import com.backend.bank.repository.AccountRepository;
import com.backend.bank.repository.CustomerRepository;
import com.backend.bank.repository.EmailChangeTokenRepository;
import com.backend.bank.repository.PhoneChangeTokenRepository;
import com.backend.bank.service.intf.CustomerService;
import com.backend.bank.service.intf.NotificationService;
import com.backend.bank.service.intf.OtpService;
import com.backend.bank.utils.EmailUtils;
import com.backend.bank.utils.RequestValidator;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Primary;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Primary
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService, UserDetailsService {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final AccountRepository accountRepository;

    private final NotificationService notificationService;

    private final EmailChangeTokenRepository emailChangeTokenRepository;

    private final PhoneChangeTokenRepository phoneChangeTokenRepository;

    private final OtpService otpService;

    private final RequestValidator<ChangePasswordRequest> changePasswordRequestValidator;

    private final RequestValidator<ChangeEmailRequest> changeEmailRequestValidator;

    private final RequestValidator<ChangePhoneNumberRequest> changePhoneNumberRequestValidator;

    private final RequestValidator<RegisterNewCardRequest> registerNewCardRequestValidator;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(username)
                .or(() -> customerRepository.findByPhoneNumber(username))
                .or(() -> customerRepository.findByAccount_AccountNumber(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                customer.getEmail(),
                customer.getPassword(),
                new ArrayList<>(customer.getAuthorities())
        );
    }

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            noRollbackFor = MailException.class
    )
    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {

        Set<String> violations = changePasswordRequestValidator.validate(request);
        if (!violations.isEmpty()) {
            throw new InputViolationException(String.join("\n", violations));
        }

        Account accountRequest = accountRepository.findByAccountHolder_Email(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        boolean isCorrectPassword = passwordEncoder.matches(request.oldPassword(), accountRequest.getAccountHolder().getPassword());
        if (!isCorrectPassword) {
            throw new BadCredentialsException("Wrong password");
        }

        boolean isPasswordConfirmed = request.newPassword().equals(request.confirmNewPassword());
        if (!isPasswordConfirmed) {
            throw new BadCredentialsException("Password does not match");
        }

        accountRequest.getAccountHolder().setPassword(passwordEncoder.encode(request.newPassword()));
        accountRepository.save(accountRequest);

        sendChangedPasswordSuccessEmail(request, new Date());

        return new ChangePasswordResponse("Changed password successfully!");
    }

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            noRollbackFor = MailException.class
    )
    public ChangeEmailResponse changeEmail(ChangeEmailRequest request) {

        Set<String> violations = changeEmailRequestValidator.validate(request);
        if (!violations.isEmpty()) {
            throw new InputViolationException(String.join("\n", violations));
        }

        Account account = accountRepository.findByAccountHolder_Email(request.oldEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        boolean isCorrectPassword = passwordEncoder.matches(request.confirmPassword(), account.getAccountHolder().getPassword());
        if (!isCorrectPassword) {
            throw new BadCredentialsException("Wrong password");
        }

        String token = UUID.randomUUID().toString();
        EmailChangeToken emailChangeToken = new EmailChangeToken();
        emailChangeToken.setToken(token);
        emailChangeToken.setNewEmail(request.newEmail());
        emailChangeToken.setOldEmail(request.oldEmail());
        emailChangeToken.setExpiryDate(LocalDateTime.from(LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.UTC)));
        emailChangeTokenRepository.save(emailChangeToken);

        String confirmLink = "https://localhost:8080/confirm-email-change?token=" + token;
        String emailContent = EmailUtils.sendChangeEmailConfirmation(request.newEmail(), confirmLink);

        EmailDetails changeEmailMessage = new EmailDetails();
        changeEmailMessage.setReceiver(request.oldEmail());
        changeEmailMessage.setSubject("CHANGE EMAIL");
        changeEmailMessage.setBody(emailContent);
        notificationService.sendEmailToCustomer(changeEmailMessage);

        return new ChangeEmailResponse("Confirmation link sent to new email.", confirmLink);
    }

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            noRollbackFor = MailException.class
    )
    public String confirmEmailChange(String token) {
        EmailChangeToken emailChangeToken = (EmailChangeToken) emailChangeTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        boolean isTokenExpired = emailChangeToken.getExpiryDate().toInstant(ZoneOffset.UTC).isBefore(Instant.now());
        if (isTokenExpired) {
            throw new IllegalArgumentException("Token expired");
        }

        Account account = accountRepository.findByAccountHolder_Email(emailChangeToken.getOldEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        account.getAccountHolder().setEmail(emailChangeToken.getNewEmail());
        accountRepository.save(account);

        emailChangeTokenRepository.delete(emailChangeToken);

        return "Email changed successfully!";
    }

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            noRollbackFor = MailException.class
    )
    public ChangePhoneNumberResponse changePhoneNumber(ChangePhoneNumberRequest request) {

        Set<String> violations = changePhoneNumberRequestValidator.validate(request);
        if (!violations.isEmpty()) {
            throw new InputViolationException(String.join("\n", violations));
        }

        Account account = accountRepository.findByAccountHolder_PhoneNumber(request.oldPhoneNumber())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        boolean isCorrectPassword = passwordEncoder.matches(request.confirmPassword(), account.getAccountHolder().getPassword());
        if (!isCorrectPassword) {
            throw new BadCredentialsException("Wrong password");
        }

        String token = UUID.randomUUID().toString();
        PhoneChangeToken phoneChangeToken = new PhoneChangeToken();
        phoneChangeToken.setToken(token);
        phoneChangeToken.setNewPhoneNumber(request.newPhoneNumber());
        phoneChangeToken.setOldPhoneNumber(request.oldPhoneNumber());
        phoneChangeToken.setExpiryDate(LocalDateTime.from(LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.UTC)));
        phoneChangeTokenRepository.save(phoneChangeToken);

        String confirmLink = "https://localhost:8080/api/phone/confirm-phone-change/" + token;
        String emailContent = EmailUtils.sendChangePhoneNumberConfirmation(request.newPhoneNumber(), confirmLink);

        EmailDetails changePhoneNumberEmail = new EmailDetails();
        changePhoneNumberEmail.setReceiver(account.getAccountHolder().getEmail());
        changePhoneNumberEmail.setSubject("CHANGE PHONE NUMBER");
        changePhoneNumberEmail.setBody(emailContent);
        notificationService.sendEmailToCustomer(changePhoneNumberEmail);

        String otp = otpService.generateOTP(request.newPhoneNumber());
        otpService.sendOTP(request.newPhoneNumber(), otp);

        return new ChangePhoneNumberResponse("Confirmation link sent to email and OTP sent to new phone number.");
    }

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            noRollbackFor = MailException.class
    )
    public String confirmPhoneNumberChangeByLinkOnEmail(String token) {
        PhoneChangeToken phoneChangeToken = phoneChangeTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        boolean isTokenExpired = phoneChangeToken.getExpiryDate().toInstant(ZoneOffset.UTC).isBefore(Instant.now());
        if (isTokenExpired) {
            throw new IllegalArgumentException("Token expired");
        }

        Account account = accountRepository.findByAccountHolder_PhoneNumber(phoneChangeToken.getOldPhoneNumber())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        account.getAccountHolder().setPhoneNumber(phoneChangeToken.getNewPhoneNumber());
        accountRepository.save(account);

        phoneChangeTokenRepository.delete(phoneChangeToken);

        return "Phone number changed successfully!";
    }

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            noRollbackFor = MailException.class
    )
    public String confirmPhoneNumberChangeByOTP(String otp, String newPhoneNumber) {
        boolean isValid = otpService.validateOTP(newPhoneNumber, otp);

        if (!isValid) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        Account account = accountRepository.findByAccountHolder_PhoneNumber(newPhoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        account.getAccountHolder().setPhoneNumber(newPhoneNumber);
        accountRepository.save(account);

        otpService.invalidateOTP(newPhoneNumber);

        return "Phone number changed successfully!";
    }

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            noRollbackFor = MailException.class
    )
    public RegisterNewCardResponse registerNewCard(RegisterNewCardRequest registerNewCardRequest) {

        Set<String> violations = registerNewCardRequestValidator.validate(registerNewCardRequest);
        if (!violations.isEmpty()) {
            throw new InputViolationException(String.join("\n", violations));
        }

        return null;
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new AccountNotExistException("Customer not found"));
    }

    private void sendChangedPasswordSuccessEmail(ChangePasswordRequest changePasswordRequest, Date changedPasswordDate) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setReceiver(changePasswordRequest.email());
        emailDetails.setSubject("Signup successful!");
        emailDetails.setBody(EmailUtils.sendEmailOnChangePassword(changePasswordRequest, changedPasswordDate));
        notificationService.sendEmailToCustomer(emailDetails);
    }
}