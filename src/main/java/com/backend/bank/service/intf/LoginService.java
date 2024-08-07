package com.backend.bank.service.intf;

import com.backend.bank.dto.request.LoginRequest;
import com.backend.bank.dto.response.LoginResponse;
import com.backend.bank.exception.AccountNotExistException;

import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public interface LoginService {
    CompletableFuture<LoginResponse> login(LoginRequest loginRequest) throws AccountNotExistException;
}
