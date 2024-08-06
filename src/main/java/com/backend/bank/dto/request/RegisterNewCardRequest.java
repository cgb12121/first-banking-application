package com.backend.bank.dto.request;

import com.backend.bank.entity.Customer;
import com.backend.bank.entity.constant.CardType;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterNewCardRequest {
    private Long customerId;
    private String cardNumber;
    private CardType cardType;
    private BigDecimal creditLimit;
    private String expirationDate;
    @ManyToOne
    Customer customer;
}
