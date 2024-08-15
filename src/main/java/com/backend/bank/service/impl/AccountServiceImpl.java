package com.backend.bank.service.impl;

import com.backend.bank.dto.request.UpdateCustomerInfoRequest;
import com.backend.bank.dto.request.UpgradeAccountRequest;
import com.backend.bank.dto.response.UpdateCustomerInfoResponse;
import com.backend.bank.dto.response.UpgradeAccountResponse;
import com.backend.bank.service.intf.AccountService;

import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    public UpgradeAccountResponse upgradeAccount(UpgradeAccountRequest request) {
        return null;
    }

    public UpdateCustomerInfoResponse updateCustomerInfo(UpdateCustomerInfoRequest request) {
        return null;
    }
}
