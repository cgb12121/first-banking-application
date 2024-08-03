package com.backend.bank.utils;

public class EmailUtils {
    public static final String EMAIL_CREATE_ACCOUNT_EVENT = "ACCOUNT CREATION";

    public static String emailAccountCreationSuccess(User user) {
        return "Congratulation! \n " +
                "Your account has been created successfully. \n !" +
                "Your account details:" +
                user.getFirstName() + " " + user.getLastName() + "\n" +
                user.getAccount().getAccountNumber() + "\n" +
                "Created at: " + user.getAccount().getCreatedDate();
    }
}
