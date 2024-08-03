package com.backend.bank.service;

import com.backend.bank.dto.request.UserDto;
import com.backend.bank.exception.AccountAlreadyExistsException;

public interface UserService {
    UserDto register(UserDto request) throws AccountAlreadyExistsException;
}
