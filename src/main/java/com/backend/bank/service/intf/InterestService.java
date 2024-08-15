package com.backend.bank.service.intf;

import com.backend.bank.exception.AccountNotExistException;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;

@Service
public interface InterestService {
    @Transactional(rollbackFor = Exception.class, noRollbackFor = MailException.class)
    void addInterest(String accountNumber, BigDecimal interest) throws AccountNotExistException, AccountNotFoundException;
}
