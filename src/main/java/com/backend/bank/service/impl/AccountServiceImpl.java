package com.backend.bank.service.impl;

import com.backend.bank.dto.request.UpdateCustomerInfoRequest;
import com.backend.bank.dto.request.UpgradeAccountRequest;
import com.backend.bank.dto.response.UpdateCustomerInfoResponse;
import com.backend.bank.dto.response.UpgradeAccountResponse;
import com.backend.bank.entity.Account;
import com.backend.bank.entity.enums.AccountType;
import com.backend.bank.exception.AccountNotExistException;
import com.backend.bank.exception.IllegalAccountTypeException;
import com.backend.bank.exception.InputViolationException;
import com.backend.bank.repository.jpa.AccountRepository;
import com.backend.bank.service.intf.AccountService;
import com.backend.bank.utils.RequestValidator;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final RequestValidator<UpgradeAccountRequest> upgradeAccountRequestValidator;

    private final RequestValidator<UpdateCustomerInfoRequest> updateCustomerInfoRequestValidator;

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class, timeout = 120)
    public CompletableFuture<UpgradeAccountResponse> upgradeAccount(UpgradeAccountRequest request) {
        Set<String> violations = upgradeAccountRequestValidator.validate(request);
        if (!violations.isEmpty()) {
            throw new InputViolationException(String.join("\n", violations));
        }

        Account account = accountRepository.findById(request.customerId())
                .orElseThrow(() -> new AccountNotExistException("Can not find account: " + request.customerId()));

        AccountType accountTypeRequest = request.newAccountType();
        switch (accountTypeRequest) {
            case REGULAR -> account.setAccountType(AccountType.REGULAR);
            case BANK_STAFF -> account.setAccountType(AccountType.BANK_STAFF);
            case VIP -> account.setAccountType(AccountType.VIP);
            case ENTERPRISE -> account.setAccountType(AccountType.ENTERPRISE);
            case null, default -> throw new IllegalAccountTypeException("Can not find accountType: " + accountTypeRequest);
        }

        String message = "You have upgraded your account to: ";
        UpgradeAccountResponse response = new UpgradeAccountResponse(message, accountTypeRequest);

        return CompletableFuture.completedFuture(response);
    }

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class, timeout = 120)
    public CompletableFuture<UpdateCustomerInfoResponse> updateCustomerInfo(UpdateCustomerInfoRequest request) {
        Set<String> violations = updateCustomerInfoRequestValidator.validate(request);
        if (!violations.isEmpty()) {
            throw new InputViolationException(String.join("\n", violations));
        }

        Account account = accountRepository.findById(request.customerId())
                .orElseThrow(() -> new AccountNotExistException("Account not found: " + request.customerId()));

        String newFirstName = request.firstName();
        String newLastName = request.lastName();
        String newEmail = request.email();
        String newPhoneNumber = request.phoneNumber();

        account.getAccountHolder().setFirstName(newFirstName);
        account.getAccountHolder().setLastName(newLastName);
        account.getAccountHolder().setEmail(newEmail);
        account.getAccountHolder().setPhoneNumber(newPhoneNumber);
        accountRepository.save(account);

        return CompletableFuture.completedFuture(new UpdateCustomerInfoResponse(
                newFirstName,
                newLastName,
                newEmail,
                newPhoneNumber,
                "Changed successfully"
        ));
    }

}
