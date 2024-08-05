package com.backend.bank.service.intf;

import com.backend.bank.dto.request.LoginRequest;
import com.backend.bank.dto.response.LoginResponse;
import com.backend.bank.exception.AccountNotExistException;

public interface AuthenticationService {
    LoginResponse login(LoginRequest loginRequest) throws AccountNotExistException;
}
