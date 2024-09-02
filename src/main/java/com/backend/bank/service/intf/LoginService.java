package com.backend.bank.service.intf;

import com.backend.bank.dto.request.LoginRequest;
import com.backend.bank.dto.response.LoginResponse;
import com.backend.bank.exception.AccountBannedException;
import com.backend.bank.exception.AccountInactiveException;
import com.backend.bank.exception.AccountNotExistException;

import com.backend.bank.exception.InputViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public interface LoginService {
    CompletableFuture<LoginResponse> login(LoginRequest loginRequest)
            throws AccountNotExistException, AccountBannedException, AccountInactiveException,
            InputViolationException;
}
