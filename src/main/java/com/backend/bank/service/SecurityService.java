package com.backend.bank.service;

import com.backend.bank.entity.Account;
import com.backend.bank.entity.Customer;
import com.backend.bank.repository.AccountRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final AccountRepository accountRepository;

    public SecurityService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public boolean canAccessAccount(Long accountId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account account = accountRepository.findById(accountId).orElse(null);

        if (account == null) {
            return false;
        }

        Customer customer = account.getAccountHolder();
        return customer.getUsername().equals(userDetails.getUsername());
    }
}
