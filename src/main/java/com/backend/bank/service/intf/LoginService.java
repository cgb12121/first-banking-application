package com.backend.bank.service.intf;

import com.backend.bank.dto.request.LoginRequest;
import com.backend.bank.dto.response.LoginResponse;
import com.backend.bank.exception.AccountNotExistException;

import org.springframework.stereotype.Service;

@Service
public interface LoginService {
    LoginResponse login(LoginRequest loginRequest) throws AccountNotExistException;
}
