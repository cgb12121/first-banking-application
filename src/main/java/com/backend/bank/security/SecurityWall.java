package com.backend.bank.security;

import com.backend.bank.entity.Account;
import com.backend.bank.entity.Customer;
import com.backend.bank.exception.AccountNotExistException;
import com.backend.bank.exception.UnauthorizedAccessException;
import com.backend.bank.repository.AccountRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityWall {

    private final AccountRepository accountRepository;

    public SecurityWall(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public boolean canAccessAccount(Long accountId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotExistException("This Account does not exist" + accountId));

        if (account == null) {
            throw new UnauthorizedAccessException("You are not authorized to access this resource!");
        }

        Customer customer = account.getAccountHolder();
        return customer.getUsername().equals(userDetails.getUsername());
    }
}
