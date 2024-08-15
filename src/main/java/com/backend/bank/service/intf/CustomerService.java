package com.backend.bank.service.intf;

import com.backend.bank.dto.request.ChangeEmailRequest;
import com.backend.bank.dto.request.ChangePasswordRequest;
import com.backend.bank.dto.request.ChangePhoneNumberRequest;
import com.backend.bank.dto.request.RegisterNewCardRequest;
import com.backend.bank.dto.response.ChangeEmailResponse;
import com.backend.bank.dto.response.ChangePasswordResponse;
import com.backend.bank.dto.response.ChangePhoneNumberResponse;
import com.backend.bank.dto.response.RegisterNewCardResponse;

import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface CustomerService {
    @Transactional(rollbackFor = Exception.class, noRollbackFor = MailException.class)
    ChangePasswordResponse changePassword(ChangePasswordRequest request);

    @Transactional(rollbackFor = Exception.class, noRollbackFor = MailException.class)
    ChangeEmailResponse changeEmail(ChangeEmailRequest request);

    @Transactional(rollbackFor = Exception.class, noRollbackFor = MailException.class)
    String confirmEmailChange(String token);

    @Transactional(rollbackFor = Exception.class, noRollbackFor = MailException.class)
    ChangePhoneNumberResponse changePhoneNumber(ChangePhoneNumberRequest request);

    @Transactional(rollbackFor = Exception.class, noRollbackFor = MailException.class)
    String confirmPhoneNumberChangeByLinkOnEmail(String token);

    @Transactional(rollbackFor = Exception.class, noRollbackFor = MailException.class)
    String confirmPhoneNumberChangeByOTP(String otp, String newPhoneNumber);

    @Transactional(rollbackFor = Exception.class, noRollbackFor = MailException.class)
    RegisterNewCardResponse registerNewCard(RegisterNewCardRequest registerNewCardRequest);
}
