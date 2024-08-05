package com.backend.bank.utils;

import com.backend.bank.dto.request.SignupRequest;

import java.time.LocalTime;

public class EmailUtils {

    public static String emailAccountCreationSuccess(SignupRequest user) {
        return "Congratulation! \n " +
                "Your account has been created successfully. \n !" +
                "Your account details:" +
                user.getFirstName() + " " + user.getLastName() + "\n" +
                user.getAccount().getAccountNumber() + "\n" +
                "Created at: " + LocalTime.now();
    }

}
