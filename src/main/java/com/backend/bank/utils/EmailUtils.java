package com.backend.bank.utils;

import com.backend.bank.dto.request.SignupRequest;
import com.backend.bank.dto.request.TransactionRequest;
import com.backend.bank.entity.Customer;

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

    public static String sendEmailOnDeposit(Customer customer, TransactionRequest transactionRequest) {
        return transactionRequest.getType().name().toUpperCase() + "\n"
                + "Dear " + customer.getFirstName() + " " + customer.getLastName() + "\n"
                + "At: " + LocalTime.now() + "\n"
                + "You have deposited " + transactionRequest.getAmount() + " into your account" + "\n"
                + "Your current balance is: " + customer.getAccount().getBalance() + "\n"
                + "Thank you for your deposited transaction!";
    }

    public static String sendEmailOnWithdrawal(Customer customer, TransactionRequest transactionRequest) {
        return transactionRequest.getType().name().toUpperCase() + "\n"
                + "Dear " + customer.getFirstName() + " " + customer.getLastName() + "\n"
                + "At: " + LocalTime.now() + "\n"
                + "You have withdrawn " + transactionRequest.getAmount() + " from your account" + "\n"
                + "Your current balance is: " + customer.getAccount().getBalance() + "\n"
                + "If it was not you, please contact us for further support.";
    }

    public static String sendEmailOnTransfer(Customer customer, TransactionRequest transactionRequest) {
        return transactionRequest.getType().name().toUpperCase() + "\n"
                + "Dear " + customer.getFirstName() + " " + customer.getLastName() + "\n"
                + "At: " + LocalTime.now() + "\n"
                + "You have transferred " + transactionRequest.getAmount()
                + " into this account" + transactionRequest.getTransferToAccount() + "\n"
                + "Your current balance is: " + customer.getAccount().getBalance() + "\n"
                + "Thank you for your transfer transaction!" + "\n"
                + "If it was not you, please contact us for further support.";
    }

    public static String sendEmailOnReceiving(Customer customer, TransactionRequest transactionRequest, Customer transferedCustomer) {
        return transactionRequest.getType().name().toUpperCase() + "\n"
                + "Dear " + customer.getFirstName() + " " + customer.getLastName() + "\n"
                + "At: " + LocalTime.now() + "\n"
                + "You have received " + transactionRequest.getAmount()
                + " from this account" + transferedCustomer.getAccount().getAccountNumber() + "\n"
                + "Your current balance is: " + customer.getAccount().getBalance() + "\n";
    }
}
