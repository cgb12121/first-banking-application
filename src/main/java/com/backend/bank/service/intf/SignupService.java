package com.backend.bank.service.intf;

import com.backend.bank.dto.request.SignupRequest;
import com.backend.bank.dto.response.SignupResponse;
import com.backend.bank.exception.AccountAlreadyExistsException;

import com.backend.bank.exception.InputViolationException;
import com.backend.bank.exception.InvalidVerifyLink;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
public interface SignupService {
    @Transactional(rollbackFor = Exception.class, noRollbackFor = MailException.class)
    CompletableFuture<SignupResponse> signup(SignupRequest signupRequest) throws AccountAlreadyExistsException, InputViolationException;

    @Async
    void resendVerificationEmail(SignupRequest signupRequest);

    @Async(value = "verify")
    CompletableFuture<String> verifyUser(String httpRequest) throws InvalidVerifyLink;
}
