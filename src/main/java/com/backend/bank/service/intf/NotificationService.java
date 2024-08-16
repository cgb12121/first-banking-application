package com.backend.bank.service.intf;

import com.backend.bank.dto.EmailDetails;

import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    void sendEmailToCustomer(EmailDetails emailDetails);
}
