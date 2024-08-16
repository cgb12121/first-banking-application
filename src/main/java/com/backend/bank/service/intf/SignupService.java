package com.backend.bank.service.intf;

import com.backend.bank.dto.request.SignupRequest;
import com.backend.bank.dto.response.SignupResponse;
import com.backend.bank.exception.AccountAlreadyExistsException;

import com.backend.bank.exception.InputViolationException;
import com.backend.bank.exception.InvalidVerifyLinkException;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public interface SignupService {

    CompletableFuture<SignupResponse> signup(SignupRequest signupRequest)
            throws AccountAlreadyExistsException, InputViolationException;

    void resendVerificationEmail(SignupRequest signupRequest);

    CompletableFuture<String> verifyUser(String httpRequest)
            throws InvalidVerifyLinkException;
}
