package com.backend.bank.service.intf;

import org.springframework.stereotype.Service;

@Service
public interface OtpService {

    String generateOTP(String identifier);

    boolean validateOTP(String identifier, String otp);

    void sendOTP(String identifier, String otp);

    void invalidateOTP(String identifier);
}
