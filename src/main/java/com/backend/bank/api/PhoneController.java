package com.backend.bank.api;

import com.backend.bank.dto.request.ChangePhoneNumberRequest;
import com.backend.bank.dto.response.ChangePhoneNumberResponse;
import com.backend.bank.service.intf.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/phone")
public class PhoneController {

    private final CustomerService customerService;

    @PostMapping("/change")
    public ChangePhoneNumberResponse changePhoneNumber(@RequestBody ChangePhoneNumberRequest request) {
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
