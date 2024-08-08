package com.backend.bank.service.intf;

import com.backend.bank.dto.request.ChangeEmailRequest;
import com.backend.bank.dto.request.ChangePasswordRequest;
import com.backend.bank.dto.request.ChangePhoneNumberRequest;
import com.backend.bank.dto.request.RegisterNewCardRequest;
import com.backend.bank.dto.response.ChangeEmailResponse;
import com.backend.bank.dto.response.ChangePasswordResponse;
import com.backend.bank.dto.response.ChangePhoneNumberResponse;
import com.backend.bank.dto.response.RegisterNewCardResponse;
import jakarta.transaction.Transactional;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

@Service
public interface CustomerService {
    @Transactional(rollbackOn = Exception.class, dontRollbackOn = MailException.class)
    ChangePasswordResponse changePassword(ChangePasswordRequest request);

    @Transactional(rollbackOn = Exception.class, dontRollbackOn = MailException.class)
    ChangeEmailResponse changeEmail(ChangeEmailRequest request);

    @Transactional(rollbackOn = Exception.class, dontRollbackOn = MailException.class)
    String confirmEmailChange(String token);

    @Transactional(rollbackOn = Exception.class, dontRollbackOn = MailException.class)
    ChangePhoneNumberResponse changePhoneNumber(ChangePhoneNumberRequest request);

    @Transactional(rollbackOn = Exception.class, dontRollbackOn = MailException.class)
    String confirmPhoneNumberChange(String token);

    @Transactional(rollbackOn = Exception.class, dontRollbackOn = MailException.class)
    String confirmPhoneNumberChangeByOTP(String otp, String newPhoneNumber);

    @Transactional(rollbackOn = Exception.class, dontRollbackOn = MailException.class)
    RegisterNewCardResponse registerNewCard(RegisterNewCardRequest registerNewCardRequest);
}
