package com.backend.bank.service.intf;

import com.backend.bank.dto.request.SignupRequest;
import com.backend.bank.dto.response.SignupResponse;
import com.backend.bank.exception.AccountAlreadyExistsException;

import org.springframework.stereotype.Service;

@Service
public interface SignupService {
    SignupResponse signup(SignupRequest signupRequest) throws AccountAlreadyExistsException;
}
