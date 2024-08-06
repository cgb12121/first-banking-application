package com.backend.bank.service.impl;

import com.backend.bank.dto.EmailDetails;
import com.backend.bank.service.intf.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Log4j2
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private void sendEmail(EmailDetails emailDetails) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(senderEmail);
            message.setTo(emailDetails.getReceiver());
            message.setSubject(emailDetails.getSubject());
            message.setText(emailDetails.getBody());

            javaMailSender.send(message);
            log.info("[timestamp:{}] Sent {} email to: {} : {}",
                    new Date(),
                    emailDetails.getSubject().toUpperCase(),
                    emailDetails.getReceiver(),
                    emailDetails.getBody()
            );
        } catch (MailException e) {
            log.error("[timestamp:{}] {} : {} When trying to send email to: \n {} {} \n {}",
                    new Date(),
                    e.getCause(),
                    e.getMessage(),
                    emailDetails.getReceiver(),
                    emailDetails.getSubject(),
                    emailDetails.getBody()
            );
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void sendEmailOnSignUp(EmailDetails emailDetails) {
        sendEmail(emailDetails);
    }

    @Override
    public void sendEmailOnTransaction(EmailDetails emailDetails) {
        sendEmail(emailDetails);
    }

    @Override
    public void sendEmailOnLogin(EmailDetails emailDetails) {
        sendEmail(emailDetails);
    }

    @Override
    public void sendEmailOnResetPassword(EmailDetails emailDetails) {
        sendEmail(emailDetails);
    }

    @Override
    public void sendEmailOnAccountStatus(EmailDetails emailDetails) {
        sendEmail(emailDetails);
    }

    @Override
    public void sendEmailOnAddNewCard(EmailDetails emailDetails) {
        sendEmail(emailDetails);
    }

    @Override
    public void sendEmailOnUpgradeAccountType(EmailDetails emailDetails) {
        sendEmail(emailDetails);
    }

    @Override
    public void sendEmailOnTakingLoan(EmailDetails emailDetails) {
        sendEmail(emailDetails);
    }

    @Override
    public void sendEmailOnLoanIncreasedInterest(EmailDetails emailDetails) {
        sendEmail(emailDetails);
    }

    @Override
    public void sendEmailOnLoanDecreasedInterest(EmailDetails emailDetails) {
        sendEmail(emailDetails);
    }

    @Override
    public void sendEmailOnLoanLate(EmailDetails emailDetails) {
        sendEmail(emailDetails);
    }

    @Override
    public void sendEmailOnLoanPaid(EmailDetails emailDetails) {
        sendEmail(emailDetails);
    }
}
