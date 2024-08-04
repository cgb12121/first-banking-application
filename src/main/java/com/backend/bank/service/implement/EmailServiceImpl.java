package com.backend.bank.service.implement;

import com.backend.bank.dto.EmailDetails;
import com.backend.bank.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
            log.info("Sent {} email to: {}",
                    emailDetails.getSubject().toUpperCase(),
                    emailDetails.getReceiver()
            );
        } catch (MailException e) {
            log.error("{} : {} \n When trying to send email to: \n {} {}",
                    e.getCause(),
                    e.getMessage(),
                    emailDetails.getReceiver(),
                    emailDetails.getSubject()
            );
            throw new RuntimeException(e.getMessage());
        }
    }

}
