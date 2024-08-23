package com.backend.bank.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.backend.bank.entity.constant.CardType;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public record CardRequest(
        @NotNull(message = "Card number can not be null")
        String cardNumber,

        @NotNull(message = "Card Type must not be null")
        CardType cardType,

        @Future(message = "Expiry date must be in the future")
        LocalDate expiryDate,

        @DecimalMax(value = "999999999999999999999999", message = "Credit limit must be less than or equal to the specified value")
        BigDecimal creditLimit) {

        @SuppressWarnings("all")
        public static CardRequestBuilder builder() {
                return new CardRequestBuilder();
        }

        @SuppressWarnings("all")
        public static class CardRequestBuilder {

                private String cardNumber;
                private CardType cardType;
                private LocalDate expiryDate;
                private BigDecimal creditLimit;

                public CardRequestBuilder cardNumber(String cardNumber, CardType cardType) {
                        this.cardNumber = cardNumber;
                        this.cardType = cardType;
                        this.expiryDate = calculateExpiryDate(cardType);
                        this.creditLimit = calculateCreditLimit(cardType);
                        return this;
                }

                private LocalDate calculateExpiryDate(CardType cardType) {
                        if (cardType == CardType.CREDIT) {
                                return LocalDate.now().plusMonths(3);
                        } else if (cardType == CardType.DEBIT) {
                                return LocalDate.now().plusYears(1);
                        } else {
                                return null;
                        }
                }

                private BigDecimal calculateCreditLimit(CardType cardType) {
                        Double limit;
                        if (cardType == CardType.CREDIT) {
                                limit = 100000000.0; //100 Million
                                return BigDecimal.valueOf(limit);
                        } else if (cardType == CardType.DEBIT) {
                                limit = 1000000000.0; // 1 Billion
                                return BigDecimal.valueOf(limit);
                        }

                        return null;
                }

                public CardRequest build() {
                        return new CardRequest(cardNumber, cardType, expiryDate, creditLimit);
                }
        }

}
