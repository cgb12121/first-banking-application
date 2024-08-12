package com.backend.bank.utils;

import com.backend.bank.dto.request.ChangePasswordRequest;
import com.backend.bank.dto.request.SignupRequest;
import com.backend.bank.dto.request.TransactionRequest;
import com.backend.bank.entity.Account;
import com.backend.bank.entity.Customer;

import java.math.BigDecimal;
import java.time.LocalDate;
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
                .append(user.firstName()).append(" ").append(user.lastName()).append(NEW_LINE)
                .append(user.account().accountNumber()).append(NEW_LINE)
                .append("Created at: ").append(now).append(NEW_LINE)
                .append("Thank you for registering your account.");
        return message.toString();
    }

    public static String sendEmailOnDeposit(Customer customer, TransactionRequest transactionRequest, Date now) {
        AtomicReference<StringBuilder> message = new AtomicReference<>(new StringBuilder());
        message.get().append(transactionRequest.type().name().toUpperCase()).append(NEW_LINE)
                .append("Dear ").append(customer.getFirstName()).append(" ").append(customer.getLastName()).append(NEW_LINE)
                .append("At: ").append(now).append(NEW_LINE)
                .append("You have deposited ").append(transactionRequest.amount()).append(" into your account.").append(NEW_LINE)
                .append("Your current balance is: ").append(customer.getAccount().getBalance()).append(NEW_LINE)
                .append("Thank you for your deposit transaction!");
        return message.toString();
    }

    public static String sendEmailOnWithdrawal(Customer customer, TransactionRequest transactionRequest, Date now) {
        AtomicReference<StringBuilder> message = new AtomicReference<>(new StringBuilder());
        message.get().append(transactionRequest.type().name().toUpperCase()).append(NEW_LINE)
                .append("Dear ").append(customer.getFirstName()).append(" ").append(customer.getLastName()).append(NEW_LINE)
                .append("At: ").append(now).append(NEW_LINE)
                .append("You have withdrawn ").append(transactionRequest.amount()).append(" from your account.").append(NEW_LINE)
                .append("Your current balance is: ").append(customer.getAccount().getBalance()).append(NEW_LINE)
                .append("If it was not you, please contact us for further support.");
        return message.toString();
    }

    public static String sendEmailOnTransfer(Customer customer, TransactionRequest transactionRequest, Date now) {
        AtomicReference<StringBuilder> message = new AtomicReference<>(new StringBuilder());
        message.get().append(transactionRequest.type().name().toUpperCase()).append(NEW_LINE)
                .append("Dear ").append(customer.getFirstName()).append(" ").append(customer.getLastName()).append(NEW_LINE)
                .append("At: ").append(now).append(NEW_LINE)
                .append("You have transferred ").append(transactionRequest.amount()).append(" into this account: ").append(transactionRequest.transferToAccount()).append(NEW_LINE)
                .append("Your current balance is: ").append(customer.getAccount().getBalance()).append(NEW_LINE)
                .append("Thank you for your transfer transaction!").append(NEW_LINE)
                .append("If it was not you, please contact us for further support.");
        return message.toString();
    }

    public static String sendEmailOnReceiving(Customer receiver, TransactionRequest transactionRequest, Date now) {
        AtomicReference<StringBuilder> message = new AtomicReference<>(new StringBuilder());
        message.get().append("Dear ").append(receiver.getFirstName()).append(" ").append(receiver.getLastName()).append(NEW_LINE)
                .append("At: ").append(now).append(NEW_LINE)
                .append("You have received ").append(transactionRequest.amount()).append(" from account number ").append(transactionRequest.transferToAccount()).append(NEW_LINE)
                .append("Your current balance is: ").append(receiver.getAccount().getBalance()).append(NEW_LINE)
                .append("Thank you for using our service!");
        return message.toString();
    }

    public static String sendEmailOnChangePassword(ChangePasswordRequest changePasswordRequest, Date changedPasswordDate) {
        AtomicReference<StringBuilder> message = new AtomicReference<>(new StringBuilder());
        message.get().append("Dear ").append(changePasswordRequest.email()).append(NEW_LINE)
                .append("At: ").append(changedPasswordDate).append(NEW_LINE)
                .append("Your account password has been changed successfully. ").append(NEW_LINE)
                .append("If it was not you, please change your password again to secure your account.").append(NEW_LINE)
                .append("Thank you for using our service!");
        return message.toString();
    }

    public static String sendEmailOnReceivingInterest(Account account, BigDecimal earnedInterest, LocalDate now) {
        AtomicReference<StringBuilder> message = new AtomicReference<>(new StringBuilder());
        message.get().append("Dear ").append(account.getAccountHolder().getFirstName()).append(" ").append(account.getAccountHolder().getLastName()).append(NEW_LINE)
                .append("You have received monthly interest.").append("Amount: ").append(earnedInterest).append(NEW_LINE)
                .append("Thank you for using our service!");
        return message.toString();
    }

    public static String sendChangeEmailConfirmation(String newEmail, String confirmLink) {
        AtomicReference<StringBuilder> message = new AtomicReference<>(new StringBuilder());
        message.get().append("CHANGE EMAIL CONFIRMATION").append(System.lineSeparator())
                .append("Dear user,").append(System.lineSeparator())
                .append("Your account have requested to change to a new email: ").append(newEmail).append(System.lineSeparator())
                .append("Please click the following link to confirm your email change:").append(System.lineSeparator())
                .append(confirmLink).append(System.lineSeparator())
                .append("If it was not you, please contact us for further support.")
                .append("Thank you for using our service!");
        return message.toString();
    }

    public static String sendChangePhoneNumberConfirmation(String newPhoneNumber, String confirmLink) {
        AtomicReference<StringBuilder> message = new AtomicReference<>(new StringBuilder());
        message.get().append("CHANGE PHONE NUMBER CONFIRMATION").append(System.lineSeparator())
                .append("Dear user,").append(System.lineSeparator())
                .append("Your account have requested to change to a new phone number: ").append(newPhoneNumber).append(System.lineSeparator())
                .append("Please click the following link to confirm your phone number change:").append(System.lineSeparator())
                .append(confirmLink).append(System.lineSeparator())
                .append("Thank you for using our service!");
        return message.toString();
    }
}
