package com.backend.bank.api;

import com.backend.bank.dto.request.ChangeEmailRequest;
import com.backend.bank.dto.response.ChangeEmailResponse;
import com.backend.bank.service.intf.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/email")
public class EmailController {

    private final CustomerService customerService;

    @PostMapping("/change")
    public ChangeEmailResponse changeEmail(@RequestBody ChangeEmailRequest request) {
        return this.customerService.changeEmail(request);
    }

    @GetMapping("/confirm-email-change/{token}")
    public String confirmEmailChange(@PathVariable String token) {
        return this.customerService.confirmEmailChange(token);
    }
}
