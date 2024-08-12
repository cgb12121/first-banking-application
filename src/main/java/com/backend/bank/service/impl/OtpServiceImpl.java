package com.backend.bank.service.impl;

import com.backend.bank.service.intf.OtpService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class OtpServiceImpl implements OtpService {

    private final Map<String, String> otpCache = new HashMap<>();

    private final Map<String, Long> otpExpiryCache = new HashMap<>();

    private static final int EXPIRATION_TIME = 5; // 5 minutes

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    public OtpServiceImpl() {
        Twilio.init(accountSid, authToken);
    }

    // TODO: may generate from else into json format then use twilio.Message.fromJson() to convert to message.
    @Override
    public String generateOTP(String identifier) {
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(1000000)); // 6-digit OTP
        otpCache.put(identifier, otp);
        otpExpiryCache.put(identifier, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(EXPIRATION_TIME));
        return otp;
    }

    @Override
    public boolean validateOTP(String identifier, String otp) {
        String cachedOtp = otpCache.get(identifier);
        Long expiryTime = otpExpiryCache.get(identifier);

        return cachedOtp != null && cachedOtp.equals(otp) && expiryTime != null && System.currentTimeMillis() < expiryTime;
    }

    @Override
    public void sendOTP(String identifier, String otp) {
        Message message = Message.creator(
                new PhoneNumber(identifier),  // to
                new PhoneNumber(twilioPhoneNumber),  // from
                "Your OTP is: " + otp +  // message body
                "\n It will expire in 5 minutes."
        ).create();

        log.info("Sent OTP {} to {}. SID: {}",
                otp,
                identifier,
                message.getSid()
        );
    }

    @Override
    public void invalidateOTP(String identifier) {
        otpCache.remove(identifier);
        otpExpiryCache.remove(identifier);
    }
}
