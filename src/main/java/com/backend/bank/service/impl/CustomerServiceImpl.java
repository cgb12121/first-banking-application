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
import com.backend.bank.repository.AccountRepository;
import com.backend.bank.repository.CustomerRepository;
import com.backend.bank.repository.EmailChangeTokenRepository;
import com.backend.bank.repository.PhoneChangeTokenRepository;
import com.backend.bank.service.intf.CustomerService;

import com.backend.bank.service.intf.EmailService;
import com.backend.bank.service.intf.OtpService;
import com.backend.bank.utils.EmailUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.mail.MailException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService, UserDetailsService {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    private final AccountRepository accountRepository;

    private final EmailService emailService;

    private final EmailChangeTokenRepository emailChangeTokenRepository;

    private final PhoneChangeTokenRepository phoneChangeTokenRepository;

    private final OtpService otpService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> customer = customerRepository.findByEmail(username)
                .or(() -> customerRepository.findByPhoneNumber(username))
                .or(() -> customerRepository.findByAccount_AccountNumber(username));

        return customer.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    @Transactional(rollbackOn = Exception.class, dontRollbackOn = MailException.class)
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

    @Override
    @Transactional(rollbackOn = Exception.class, dontRollbackOn = MailException.class)
    public ChangeEmailResponse changeEmail(ChangeEmailRequest request) {
        Account account = accountRepository.findByAccountHolder_Email(request.getOldEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        if (!passwordEncoder.matches(request.getConfirmPassword(), account.getAccountHolder().getPassword())) {
            throw new BadCredentialsException("Wrong password");
        }

        String token = UUID.randomUUID().toString();
        EmailChangeToken emailChangeToken = new EmailChangeToken();
        emailChangeToken.setToken(token);
        emailChangeToken.setNewEmail(request.getNewEmail());
        emailChangeToken.setOldEmail(request.getOldEmail());
        emailChangeToken.setExpiryDate(new Date(System.currentTimeMillis() + 3600000)); // 1 hour
        emailChangeTokenRepository.save(emailChangeToken);

        String confirmLink = "https://localhost:8080/confirm-email-change?token=" + token;
        String emailContent = EmailUtils.sendChangeEmailConfirmation(request.getNewEmail(), confirmLink);

        EmailDetails changeEmailMessage = new EmailDetails();
        changeEmailMessage.setReceiver(request.getOldEmail());
        changeEmailMessage.setSubject("CHANGE EMAIL");
        changeEmailMessage.setBody(emailContent);
        emailService.sendEmailToCustomer(changeEmailMessage);

        return new ChangeEmailResponse("Confirmation link sent to new email.", confirmLink);
    }

    @Override
    @Transactional(rollbackOn = Exception.class, dontRollbackOn = MailException.class)
    public String confirmEmailChange(String token) {
        EmailChangeToken emailChangeToken = (EmailChangeToken) emailChangeTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (emailChangeToken.getExpiryDate().before(new Date())) {
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
    @Transactional(rollbackOn = Exception.class, dontRollbackOn = MailException.class)
    public ChangePhoneNumberResponse changePhoneNumber(ChangePhoneNumberRequest request) {
        Account account = accountRepository.findByAccountHolder_PhoneNumber(request.getOldPhoneNumber())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        if (!passwordEncoder.matches(request.getConfirmPassword(), account.getAccountHolder().getPassword())) {
            throw new BadCredentialsException("Wrong password");
        }

        String token = UUID.randomUUID().toString();
        PhoneChangeToken phoneChangeToken = new PhoneChangeToken();
        phoneChangeToken.setToken(token);
        phoneChangeToken.setNewPhoneNumber(request.getNewPhoneNumber());
        phoneChangeToken.setOldPhoneNumber(request.getOldPhoneNumber());
        phoneChangeToken.setExpiryDate(new Date(System.currentTimeMillis() + 3600000)); // 1 hour expiry
        phoneChangeTokenRepository.save(phoneChangeToken);

        String confirmLink = "https://localhost:8080/api/phone/confirm-phone-change/" + token;
        String emailContent = EmailUtils.sendChangePhoneNumberConfirmation(request.getNewPhoneNumber(), confirmLink);

        EmailDetails changePhoneNumberEmail = new EmailDetails();
        changePhoneNumberEmail.setReceiver(account.getAccountHolder().getEmail());
        changePhoneNumberEmail.setSubject("CHANGE PHONE NUMBER");
        changePhoneNumberEmail.setBody(emailContent);
        emailService.sendEmailToCustomer(changePhoneNumberEmail);

        String otp = otpService.generateOTP(request.getNewPhoneNumber());
        otpService.sendOTP(request.getNewPhoneNumber(), otp);

        return new ChangePhoneNumberResponse("Confirmation link sent to email and OTP sent to new phone number.", otp);
    }

    @Override
    @Transactional(rollbackOn = Exception.class, dontRollbackOn = MailException.class)
    public String confirmPhoneNumberChange(String token) {
        PhoneChangeToken phoneChangeToken = phoneChangeTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (phoneChangeToken.getExpiryDate().before(new Date())) {
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
    @Transactional(rollbackOn = Exception.class, dontRollbackOn = MailException.class)
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
    @Transactional(rollbackOn = Exception.class, dontRollbackOn = MailException.class)
    public RegisterNewCardResponse registerNewCard(RegisterNewCardRequest registerNewCardRequest) {
        return null;
    }

    private void sendChangedPasswordSuccessEmail(ChangePasswordRequest changePasswordRequest, Date changedPasswordDate) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setReceiver(changePasswordRequest.getEmail());
        emailDetails.setSubject("Signup successful!");
        emailDetails.setBody(EmailUtils.sendEmailOnChangePassword(changePasswordRequest, changedPasswordDate));
        emailService.sendEmailToCustomer(emailDetails);
    }
}