package com.backend.bank.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.backend.bank.entity.constant.CardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardRequest {
    private String cardNumber;
    private CardType cardType;
    private LocalDate expiryDate;
    private BigDecimal creditLimit;
    private BigDecimal balance;
}
