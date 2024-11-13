package com.backend.bank.service.intf;

import com.backend.bank.dto.EmailDetails;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {

    @Async(value = "emailTaskExecutor")
    void sendEmailToCustomer(EmailDetails emailDetails);

    void sendTransactionNotification(Transaction transaction);

    void sendSecurityAlert(SecurityEvent event);
    
    void sendLowBalanceAlert(Account account);
}
