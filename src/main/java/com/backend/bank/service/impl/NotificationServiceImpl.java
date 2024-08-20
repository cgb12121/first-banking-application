package com.backend.bank.service.impl;

import com.backend.bank.dto.EmailDetails;
import com.backend.bank.service.intf.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Log4j2
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    @Async(value = "emailTaskExecutor")
    public void sendEmailToCustomer(EmailDetails emailDetails) {
        try {
            sendEmail(emailDetails);
            logEmailSentSuccessfully(emailDetails);
        } catch (MailException e) {
            logEmailSentError(emailDetails, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private void sendEmail(EmailDetails emailDetails) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(emailDetails.getReceiver());
        message.setSubject(emailDetails.getSubject());
        message.setText(emailDetails.getBody());
        javaMailSender.send(message);
    }

    private void logEmailSentSuccessfully(EmailDetails emailDetails) {
        log.info("[timestamp:{}] Sent {} email to: {} : {}",
                new Date(),
                emailDetails.getSubject().toUpperCase(),
                emailDetails.getReceiver(),
                emailDetails.getBody()
        );
    }

    private void logEmailSentError(EmailDetails emailDetails, MailException e) {
        log.error("[timestamp:{}] {} : {} When trying to send email to: \n {} {} \n {}",
                new Date(),
                e.getCause(),
                e.getMessage(),
                emailDetails.getReceiver(),
                emailDetails.getSubject().toUpperCase(),
                emailDetails.getBody()
        );
    }

}
