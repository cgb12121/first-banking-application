package com.backend.bank.service.impl;

import com.backend.bank.entity.Account;
import com.backend.bank.exception.AccountNotExistException;
import com.backend.bank.repository.AccountRepository;
import com.backend.bank.service.intf.InterestService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InterestServiceImpl implements InterestService {

    private final AccountRepository accountRepository;

    /**
     * {@code Add interest} to the users' account based on {@code user's interest}.
     * @param accountNumber user's account number
     * @param interest  interest rate
     *
     * @throws AccountNotExistException if the account does not exist.
     */
    @Override
    @Transactional(
            rollbackOn = Exception.class,
            dontRollbackOn = MailException.class
    )
    public void addInterest(String accountNumber, BigDecimal interest) throws AccountNotExistException {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotExistException("Account not found"));
        BigDecimal balance = account.getBalance();
        BigDecimal balanceAfterInterest = balance.add(interest);

        account.setBalance(balanceAfterInterest);
        accountRepository.save(account);
    }
}
