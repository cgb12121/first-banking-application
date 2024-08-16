package com.backend.bank.service.impl;

import com.backend.bank.dto.request.UpdateCustomerInfoRequest;
import com.backend.bank.dto.request.UpgradeAccountRequest;
import com.backend.bank.dto.response.UpdateCustomerInfoResponse;
import com.backend.bank.dto.response.UpgradeAccountResponse;
import com.backend.bank.exception.InputViolationException;
import com.backend.bank.repository.AccountRepository;
import com.backend.bank.service.intf.AccountService;

import com.backend.bank.utils.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final RequestValidator<UpgradeAccountRequest> upgradeAccountRequestValidator;

    private final RequestValidator<UpdateCustomerInfoRequest> updateCustomerInfoRequestValidator;

    public UpgradeAccountResponse upgradeAccount(UpgradeAccountRequest request) {
        Set<String> violations = upgradeAccountRequestValidator.validate(request);
        if (!violations.isEmpty()) {
            throw new InputViolationException(String.join("\n", violations));
        }
        return null;
    }

    public UpdateCustomerInfoResponse updateCustomerInfo(UpdateCustomerInfoRequest request) {
        Set<String> violations = updateCustomerInfoRequestValidator.validate(request);
        if (!violations.isEmpty()) {
            throw new InputViolationException(String.join("\n", violations));
        }
        return null;
    }
}
