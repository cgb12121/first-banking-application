package com.backend.bank.service.impl;

import com.backend.bank.entity.Account;
import com.backend.bank.repository.AccountRepository;
import com.backend.bank.service.intf.InterestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;

@Log4j2
@Service
@RequiredArgsConstructor
public class InterestServiceImpl implements InterestService {

    private final AccountRepository accountRepository;

    /**
     * {@code Add interest} to the users' account based on {@code user's interest}.
     *
     * @param accountNumber user's account number
     * @param interest  interest rate
     */
    @Override
    @Transactional(
            rollbackFor = Exception.class,
            noRollbackFor = MailException.class
    )
    public void addInterest(String accountNumber, BigDecimal interest) throws AccountNotFoundException {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Can't find account " + accountNumber));

        BigDecimal balance = account.getBalance();
        BigDecimal balanceAfterInterest = balance.add(interest);

        account.setBalance(balanceAfterInterest);
        accountRepository.save(account);
    }
}
