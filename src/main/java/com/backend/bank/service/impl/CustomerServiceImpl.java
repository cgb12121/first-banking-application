package com.backend.bank.service.impl;

import com.backend.bank.entity.Customer;
import com.backend.bank.repository.CustomerRepository;
import com.backend.bank.service.intf.CustomerService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService, UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> customer = customerRepository.findByEmail(username)
                .or(() -> customerRepository.findByPhoneNumber(username))
                .or(() -> customerRepository.findByAccount_AccountNumber(username));

        return customer.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void changePassword(String newPassword) {

    }

    public void changeEmail(String newEmail) {

    }

    public void changePhoneNumber(String newPhoneNumber) {

    }

}