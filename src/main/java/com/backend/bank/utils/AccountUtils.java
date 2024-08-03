package com.backend.bank.utils;

import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_ALREADY_EXISTS = "Account already exists";
    public static final String ACCOUNT_NOT_EXISTS = "Account not exists";

    private static final int MIN_RANGE = 10000;
    private static final int MAX_RANGE = 99999;

    public static String randomAccountNumber() {
        Year currentYear = Year.now();
        int randomNumber = (int) (Math.random() * MAX_RANGE) + MIN_RANGE;

        return "80" + currentYear + randomNumber;
    }
}
