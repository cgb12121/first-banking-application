package com.backend.bank.api;

import com.backend.bank.dto.request.ChangePhoneNumberRequest;
import com.backend.bank.dto.response.ChangePhoneNumberResponse;
import com.backend.bank.service.intf.CustomerService;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/phone")
public class PhoneController {

    CustomerService customerService;

    @PostMapping("/change")
    public ChangePhoneNumberResponse changePhoneNumber(@RequestBody @Valid ChangePhoneNumberRequest request) {
        return customerService.changePhoneNumber(request);
    }

    @GetMapping("/confirm-phone-change/{token}")
    public String confirmPhoneNumberChange(@PathVariable String token) {
        return customerService.confirmPhoneNumberChangeByLinkOnEmail(token);
    }

    @PostMapping("/confirm-phone-change-otp")
    public String confirmPhoneNumberChangeByOTP(@RequestParam String otp, @RequestParam String newPhoneNumber) {
        return customerService.confirmPhoneNumberChangeByOTP(otp, newPhoneNumber);
    }
}
