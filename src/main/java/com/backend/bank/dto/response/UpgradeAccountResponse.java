package com.backend.bank.dto.response;

import com.backend.bank.entity.constant.AccountType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpgradeAccountResponse {
    private AccountType newAccountType;
    private String message;
}
