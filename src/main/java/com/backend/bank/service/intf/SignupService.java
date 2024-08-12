package com.backend.bank.service.intf;

import com.backend.bank.dto.request.SignupRequest;
import com.backend.bank.dto.response.SignupResponse;
import com.backend.bank.exception.AccountAlreadyExistsException;

import com.backend.bank.exception.InvalidVerifyLink;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public interface SignupService {
    CompletableFuture<SignupResponse> signup(SignupRequest signupRequest) throws AccountAlreadyExistsException;

    @Async
    void resendVerificationEmail(SignupRequest signupRequest);

    @Async(value = "verify")
    CompletableFuture<String> verifyUser(String httpRequest) throws InvalidVerifyLink;
}
