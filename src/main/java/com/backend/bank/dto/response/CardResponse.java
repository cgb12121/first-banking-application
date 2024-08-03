package com.backend.bank.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.backend.bank.entity.constant.CardType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardResponse {
    private Long id;
    private String cardNumber;
    private CardType cardType;
    private LocalDate expiryDate;
    private BigDecimal creditLimit;
    private BigDecimal balance;
    private Long customerId;
}

