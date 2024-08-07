package com.backend.bank.utils;

import com.backend.bank.dto.request.ChangePasswordRequest;
import com.backend.bank.dto.request.SignupRequest;
import com.backend.bank.dto.request.TransactionRequest;
import com.backend.bank.entity.Customer;

import java.util.Date;

public class EmailUtils {

    public static String emailAccountCreationSuccess(SignupRequest user, Date now) {
        return "Congratulation! \n " +
                "Your account has been created successfully. \n !" +
                "Your account details:" +
                user.getFirstName() + " " + user.getLastName() + "\n" +
                user.getAccount().getAccountNumber() + "\n" +
                "Created at: " + now + "\n"
                + "Thank you for registering your account.";
    }

    public static String sendEmailOnDeposit(Customer customer, TransactionRequest transactionRequest, Date now) {
        return transactionRequest.getType().name().toUpperCase() + "\n"
                + "Dear " + customer.getFirstName() + " " + customer.getLastName() + "\n"
                + "At: " + now + "\n"
                + "You have deposited " + transactionRequest.getAmount() + " into your account" + "\n"
                + "Your current balance is: " + customer.getAccount().getBalance() + "\n"
                + "Thank you for your deposited transaction!";
    }

    public static String sendEmailOnWithdrawal(Customer customer, TransactionRequest transactionRequest, Date now) {
        return transactionRequest.getType().name().toUpperCase() + "\n"
                + "Dear " + customer.getFirstName() + " " + customer.getLastName() + "\n"
                + "At: " + now + "\n"
                + "You have withdrawn " + transactionRequest.getAmount() + " from your account" + "\n"
                + "Your current balance is: " + customer.getAccount().getBalance() + "\n"
                + "If it was not you, please contact us for further support.";
    }

    public static String sendEmailOnTransfer(Customer customer, TransactionRequest transactionRequest, Date now) {
        return transactionRequest.getType().name().toUpperCase() + "\n"
                + "Dear " + customer.getFirstName() + " " + customer.getLastName() + "\n"
                + "At: " + now + "\n"
                + "You have transferred " + transactionRequest.getAmount()
                + " into this account" + transactionRequest.getTransferToAccount() + "\n"
                + "Your current balance is: " + customer.getAccount().getBalance() + "\n"
                + "Thank you for your transfer transaction!" + "\n"
                + "If it was not you, please contact us for further support.";
    }

    public static String sendEmailOnReceiving(Customer receiver, TransactionRequest transactionRequest, Date now) {
        return "TRANSFER" + "\n"
                + "Dear " + receiver.getFirstName() + " " + receiver.getLastName() + "\n"
                + "At: " + now + "\n"
                + "You have received " + transactionRequest.getAmount()
                + " from account number " + transactionRequest.getTransferToAccount() + "\n"
                + "Your current balance is: " + receiver.getAccount().getBalance() + "\n"
                + "Thank you for using our service!";
    }

    public static String sendEmailOnChangePassword(ChangePasswordRequest changePasswordRequest, Date changedPasswordDate) {
        return "CHANGE PASSWORD" + "\n"
                + "Dear " + changePasswordRequest.getEmail() + "\n"
                + "At: " + changedPasswordDate + "\n"
                + "Your account has changed password successfully. "
                + "If it was not you, please change your password again to secure your account" + "\n"
                + "Thank you for using our service!";
    }
}
