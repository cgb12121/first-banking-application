package com.backend.bank.service.impl;

import com.backend.bank.service.intf.OtpService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpServiceImpl implements OtpService {

    private final Map<String, String> otpCache = new HashMap<>();

    private final Map<String, Long> otpExpiryCache = new HashMap<>();

    private static final int EXPIRATION_TIME = 5; // 5 minutes

    @Override
    public String generateOTP(String identifier) {
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(10000)); // Generate a 6-digit OTP
        otpCache.put(identifier, otp);
        otpExpiryCache.put(identifier, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(EXPIRATION_TIME));
        return otp;
    }

    @Override
    public boolean validateOTP(String identifier, String otp) {
        String cachedOtp = otpCache.get(identifier);
        Long expiryTime = otpExpiryCache.get(identifier);

        if (cachedOtp != null && cachedOtp.equals(otp) && expiryTime != null && System.currentTimeMillis() < expiryTime) {
            return true;
        }
        return false;
    }

    @Override
    public void sendOTP(String identifier, String otp) {
        // Implementation of sending OTP via SMS or Email
        // This could involve integrating with an SMS gateway or Email service provider
        System.out.println("Sending OTP " + otp + " to " + identifier);
    }

    @Override
    public void invalidateOTP(String identifier) {
        otpCache.remove(identifier);
        otpExpiryCache.remove(identifier);
    }
}
