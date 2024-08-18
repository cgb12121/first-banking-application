package com.backend.bank.service.intf;

import com.backend.bank.dto.request.UpdateCustomerInfoRequest;
import com.backend.bank.dto.request.UpgradeAccountRequest;
import com.backend.bank.dto.response.UpdateCustomerInfoResponse;
import com.backend.bank.dto.response.UpgradeAccountResponse;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {
    UpgradeAccountResponse upgradeAccount(UpgradeAccountRequest request);

    UpdateCustomerInfoResponse updateCustomerInfo(UpdateCustomerInfoRequest request);
}
