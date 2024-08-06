package com.backend.bank.dto.request;

import com.backend.bank.entity.constant.AccountType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpgradeAccountRequest {
    private Long customerId;
    private AccountType newAccountType;
}
