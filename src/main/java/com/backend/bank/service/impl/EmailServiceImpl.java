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

    @Override
    public void sendEmail(EmailDetails emailDetails) {
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
}
