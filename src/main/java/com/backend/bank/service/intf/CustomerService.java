package com.backend.bank.service.intf;

import com.backend.bank.dto.request.ChangeEmailRequest;
import com.backend.bank.dto.request.ChangePasswordRequest;
import com.backend.bank.dto.request.ChangePhoneNumberRequest;
import com.backend.bank.dto.request.RegisterNewCardRequest;
import com.backend.bank.dto.response.ChangeEmailResponse;
import com.backend.bank.dto.response.ChangePasswordResponse;
import com.backend.bank.dto.response.ChangePhoneNumberResponse;
import com.backend.bank.dto.response.RegisterNewCardResponse;

import org.springframework.stereotype.Service;

@Service
public interface CustomerService {
    ChangePasswordResponse changePassword(ChangePasswordRequest request);

    ChangeEmailResponse changeEmail(ChangeEmailRequest request);

    String confirmEmailChange(String token);

    ChangePhoneNumberResponse changePhoneNumber(ChangePhoneNumberRequest request);

    String confirmPhoneNumberChangeByLinkOnEmail(String token);

    String confirmPhoneNumberChangeByOTP(String otp, String newPhoneNumber);

    RegisterNewCardResponse registerNewCard(RegisterNewCardRequest registerNewCardRequest);
}
