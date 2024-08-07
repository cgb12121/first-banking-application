package com.backend.bank.utils;

import com.backend.bank.dto.request.ChangePasswordRequest;
import com.backend.bank.dto.request.SignupRequest;
import com.backend.bank.dto.request.TransactionRequest;
import com.backend.bank.entity.Customer;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("replacement")
public class EmailUtils {

    private static final String NEW_LINE = System.lineSeparator();

    public static String emailAccountCreationSuccess(SignupRequest user, Date now) {
        AtomicReference<StringBuilder> message = new AtomicReference<>(new StringBuilder());
        message.get().append("Congratulations!").append(NEW_LINE)
                .append("Your account has been created successfully.").append(NEW_LINE)
                .append("Your account details:").append(NEW_LINE)
                .append(user.getFirstName()).append(" ").append(user.getLastName()).append(NEW_LINE)
                .append(user.getAccount().getAccountNumber()).append(NEW_LINE)
                .append("Created at: ").append(now).append(NEW_LINE)
                .append("Thank you for registering your account.");
        return message.toString();
    }

    public static String sendEmailOnDeposit(Customer customer, TransactionRequest transactionRequest, Date now) {
        AtomicReference<StringBuilder> message = new AtomicReference<>(new StringBuilder());
        message.get().append(transactionRequest.getType().name().toUpperCase()).append(NEW_LINE)
                .append("Dear ").append(customer.getFirstName()).append(" ").append(customer.getLastName()).append(NEW_LINE)
                .append("At: ").append(now).append(NEW_LINE)
                .append("You have deposited ").append(transactionRequest.getAmount()).append(" into your account.").append(NEW_LINE)
                .append("Your current balance is: ").append(customer.getAccount().getBalance()).append(NEW_LINE)
                .append("Thank you for your deposit transaction!");
        return message.toString();
    }

    public static String sendEmailOnWithdrawal(Customer customer, TransactionRequest transactionRequest, Date now) {
        AtomicReference<StringBuilder> message = new AtomicReference<>(new StringBuilder());
        message.get().append(transactionRequest.getType().name().toUpperCase()).append(NEW_LINE)
                .append("Dear ").append(customer.getFirstName()).append(" ").append(customer.getLastName()).append(NEW_LINE)
                .append("At: ").append(now).append(NEW_LINE)
                .append("You have withdrawn ").append(transactionRequest.getAmount()).append(" from your account.").append(NEW_LINE)
                .append("Your current balance is: ").append(customer.getAccount().getBalance()).append(NEW_LINE)
                .append("If it was not you, please contact us for further support.");
        return message.toString();
    }

    public static String sendEmailOnTransfer(Customer customer, TransactionRequest transactionRequest, Date now) {
        AtomicReference<StringBuilder> message = new AtomicReference<>(new StringBuilder());
        message.get().append(transactionRequest.getType().name().toUpperCase()).append(NEW_LINE)
                .append("Dear ").append(customer.getFirstName()).append(" ").append(customer.getLastName()).append(NEW_LINE)
                .append("At: ").append(now).append(NEW_LINE)
                .append("You have transferred ").append(transactionRequest.getAmount()).append(" into this account: ").append(transactionRequest.getTransferToAccount()).append(NEW_LINE)
                .append("Your current balance is: ").append(customer.getAccount().getBalance()).append(NEW_LINE)
                .append("Thank you for your transfer transaction!").append(NEW_LINE)
                .append("If it was not you, please contact us for further support.");
        return message.toString();
    }

    public static String sendEmailOnReceiving(Customer receiver, TransactionRequest transactionRequest, Date now) {
        AtomicReference<StringBuilder> message = new AtomicReference<>(new StringBuilder());
        message.get().append("TRANSFER").append(NEW_LINE)
                .append("Dear ").append(receiver.getFirstName()).append(" ").append(receiver.getLastName()).append(NEW_LINE)
                .append("At: ").append(now).append(NEW_LINE)
                .append("You have received ").append(transactionRequest.getAmount()).append(" from account number ").append(transactionRequest.getTransferToAccount()).append(NEW_LINE)
                .append("Your current balance is: ").append(receiver.getAccount().getBalance()).append(NEW_LINE)
                .append("Thank you for using our service!");
        return message.toString();
    }

    public static String sendEmailOnChangePassword(ChangePasswordRequest changePasswordRequest, Date changedPasswordDate) {
        AtomicReference<StringBuilder> message = new AtomicReference<>(new StringBuilder());
        message.get().append("CHANGE PASSWORD").append(NEW_LINE)
                .append("Dear ").append(changePasswordRequest.getEmail()).append(NEW_LINE)
                .append("At: ").append(changedPasswordDate).append(NEW_LINE)
                .append("Your account password has been changed successfully. ").append(NEW_LINE)
                .append("If it was not you, please change your password again to secure your account.").append(NEW_LINE)
                .append("Thank you for using our service!");
        return message.toString();
    }
}
