package com.backend.bank.service.intf;

import com.backend.bank.exception.AccountNotExistException;
import jakarta.transaction.Transactional;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;

@Service
public interface InterestService {
    @Transactional(rollbackOn = Exception.class, dontRollbackOn = MailException.class)
    void addInterest(String accountNumber, BigDecimal interest) throws AccountNotExistException, AccountNotFoundException;
}
