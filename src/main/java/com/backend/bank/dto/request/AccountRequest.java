package com.backend.bank.dto.request;

import java.math.BigDecimal;
import com.backend.bank.entity.constant.AccountStatus;
import com.backend.bank.entity.constant.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {
    private String accountNumber;
    private BigDecimal balance;
    private AccountType accountType;
    private AccountStatus accountStatus;
}
