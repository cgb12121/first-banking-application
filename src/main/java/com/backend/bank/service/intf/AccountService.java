package com.backend.bank.service.intf;

import com.backend.bank.dto.request.UpdateCustomerInfoRequest;
import com.backend.bank.dto.request.UpgradeAccountRequest;
import com.backend.bank.dto.response.UpdateCustomerInfoResponse;
import com.backend.bank.dto.response.UpgradeAccountResponse;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public interface AccountService {
    CompletableFuture<UpgradeAccountResponse> upgradeAccount(UpgradeAccountRequest request);

    CompletableFuture<UpdateCustomerInfoResponse> updateCustomerInfo(UpdateCustomerInfoRequest request);
}
