package com.backend.bank.service.intf;

import com.backend.bank.dto.EmailDetails;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    @Async(value = "emailTaskExecutor")
    void sendEmailToCustomer(EmailDetails emailDetails);
}
