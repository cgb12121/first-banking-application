package com.backend.bank.api;

import com.backend.bank.dto.request.ChangeEmailRequest;
import com.backend.bank.dto.response.ChangeEmailResponse;
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
@RequestMapping("/customer/email")
public class EmailController {

    CustomerService customerService;

    @PostMapping("/change")
    public ChangeEmailResponse changeEmail(@RequestBody @Valid ChangeEmailRequest request) {
        return customerService.changeEmail(request);
    }

    @GetMapping("/confirm-email-change/{token}")
    public String confirmEmailChange(@PathVariable String token) {
        return customerService.confirmEmailChange(token);
    }
}
