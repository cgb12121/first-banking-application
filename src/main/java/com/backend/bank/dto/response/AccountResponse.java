package com.backend.bank.dto.response;

import java.math.BigDecimal;
import com.backend.bank.entity.constant.AccountStatus;
import com.backend.bank.entity.constant.AccountType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private AccountType accountType;
    private AccountStatus accountStatus;
    private Long customerId;
}

