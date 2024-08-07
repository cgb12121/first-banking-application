package com.backend.bank.service.intf;

import com.backend.bank.dto.request.ChangePasswordRequest;
import com.backend.bank.dto.response.ChangePasswordResponse;
import org.springframework.stereotype.Service;

@Service
public interface CustomerService {
    ChangePasswordResponse changePassword(ChangePasswordRequest request);
}
