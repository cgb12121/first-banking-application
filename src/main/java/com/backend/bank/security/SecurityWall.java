package com.backend.bank.security;

import com.backend.bank.entity.Account;
import com.backend.bank.entity.Customer;
import com.backend.bank.exception.AccountNotExistException;
import com.backend.bank.exception.UnauthorizedAccessException;
import com.backend.bank.repository.AccountRepository;
import com.backend.bank.repository.CustomerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SecurityWall {

    private final AccountRepository accountRepository;

    private final CustomerRepository customerRepository;

    public SecurityWall(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
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

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Customer customer = customerRepository.findByEmail(username).orElse(null);

            if (customer == null) {
                throw new UsernameNotFoundException("User not found: " + username);
            }

            String userName = customer.getUsername();
            String password = customer.getPassword();

            boolean accountNonExpired = customer.isAccountNonExpired();
            boolean credentialsNonExpired = customer.isCredentialsNonExpired();
            boolean accountNonLocked = customer.isAccountNonLocked();
            boolean enabled = customer.isEnabled();

            if (!accountNonExpired) {
                throw new AccountExpiredException("Account Expired");
            }

            if (!credentialsNonExpired) {
                throw new CredentialsExpiredException("Credentials Expired");
            }

            if (!accountNonLocked) {
                throw new LockedException("Account is Locked");
            }

            if (!enabled) {
                throw new DisabledException("Account is Disabled");
            }

            List<SimpleGrantedAuthority> authorities = customer
                    .getAuthorities()
                    .stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                    .collect(Collectors.toList());

            return org.springframework.security.core.userdetails.User
                    .withUsername(userName)
                    .password(password)
                    .authorities(authorities)
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(false)
                    .build();
        };
    }
}
