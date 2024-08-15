package com.backend.bank.service.impl;

import com.backend.bank.entity.StaffAccount;
import com.backend.bank.repository.StaffAccountRepository;
import com.backend.bank.service.intf.StaffService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService, UserDetailsService {

    private final StaffAccountRepository staffAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<StaffAccount> staffAccount = staffAccountRepository.findByStaffAccount(username);
        return staffAccount.orElseThrow(() -> new UsernameNotFoundException("Staff account does not exist: " + username));
    }

}