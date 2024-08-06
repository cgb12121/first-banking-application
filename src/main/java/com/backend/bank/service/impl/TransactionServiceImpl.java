package com.backend.bank.service.impl;

import com.backend.bank.dto.EmailDetails;
import com.backend.bank.dto.request.TransactionRequest;
import com.backend.bank.dto.response.TransactionResponse;
import com.backend.bank.entity.Account;
import com.backend.bank.entity.Customer;
import com.backend.bank.entity.Transaction;
import com.backend.bank.entity.constant.AccountStatus;
import com.backend.bank.entity.constant.TransactionStatus;
import com.backend.bank.entity.constant.TransactionType;
import com.backend.bank.exception.*;
import com.backend.bank.repository.AccountRepository;
import com.backend.bank.repository.TransactionRepository;
import com.backend.bank.service.intf.EmailService;
import com.backend.bank.service.intf.TransactionService;

import com.backend.bank.utils.EmailUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    private final EmailService emailService;

    @Override
    @Transactional(
            rollbackOn = Exception.class,
            dontRollbackOn = {MailException.class}
    )
    public List<TransactionResponse> getTransactionHistory(Long accountId, int page, int size) throws AccountNotExistException {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotExistException("Account not found"));
        String accountNumber = account.getAccountNumber();

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> sentTransactions = transactionRepository.findByAccountId(accountId, pageable);
        Page<Transaction> receivedTransactions = transactionRepository.findByTransferToAccount(accountNumber, pageable);

        return Stream.concat(sentTransactions.stream(), receivedTransactions.stream())
                .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(
            rollbackOn = Exception.class,
            dontRollbackOn = {MailException.class}
    )
    public TransactionResponse deposit(Long accountId, TransactionRequest transactionRequest) throws InvalidTransactionAmountException, AccountNotExistException, AccountInactiveException, AccountFrozenException, AccountBannedException, UnknownTransactionTypeException {
        validateAmount(transactionRequest.getAmount());
        Account account = validateAccount(accountId);

        Transaction transaction = createTransaction(account, transactionRequest.getAmount(), TransactionType.DEPOSIT);

        account.setBalance(account.getBalance().add(transactionRequest.getAmount()));
        accountRepository.save(account);

        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction = transactionRepository.save(transaction);

        sendTransactionSuccessEmail(account.getAccountHolder(), transactionRequest);

        return mapToResponse(transaction);
    }

    @Override
    @Transactional(
            rollbackOn = Exception.class,
            dontRollbackOn = {MailException.class}
    )
    public TransactionResponse withdraw(Long accountId, TransactionRequest transactionRequest) throws InvalidTransactionAmountException, AccountNotExistException, AccountInactiveException, AccountFrozenException, AccountBannedException, InsufficientFundsException, UnknownTransactionTypeException {
        validateAmount(transactionRequest.getAmount());
        Account account = validateAccount(accountId);

        if (account.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal");
        }

        Transaction transaction = createTransaction(account, transactionRequest.getAmount(), TransactionType.WITHDRAWAL);

        account.setBalance(account.getBalance().subtract(transactionRequest.getAmount()));
        accountRepository.save(account);

        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction = transactionRepository.save(transaction);

        sendTransactionSuccessEmail(account.getAccountHolder(), transactionRequest);

        return mapToResponse(transaction);
    }

    @Override
    @Transactional(
            rollbackOn = Exception.class,
            dontRollbackOn = {MailException.class}
    )
    public TransactionResponse transfer(Long accountId, TransactionRequest transactionRequest) throws InvalidTransactionAmountException, AccountNotExistException, AccountInactiveException, AccountFrozenException, AccountBannedException, InsufficientFundsException, UnknownTransactionTypeException {
        validateAmount(transactionRequest.getAmount());
        Account account = validateAccount(accountId);

        if (account.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds for transfer");
        }

        Account transferToAccount = accountRepository.findByAccountNumber(transactionRequest.getTransferToAccount())
                .orElseThrow(() -> new AccountNotExistException("Transfer to account not found"));

        validateAccountStatus(transferToAccount);

        Transaction transaction = createTransaction(account, transactionRequest.getAmount(), TransactionType.TRANSFER);
        transaction.setTransferToAccount(transactionRequest.getTransferToAccount());

        account.setBalance(account.getBalance().subtract(transactionRequest.getAmount()));
        transferToAccount.setBalance(transferToAccount.getBalance().add(transactionRequest.getAmount()));
        accountRepository.save(account);
        accountRepository.save(transferToAccount);

        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction = transactionRepository.save(transaction);

        sendTransactionSuccessEmail(account.getAccountHolder(), transactionRequest);

        return mapToResponse(transaction);
    }

    private void validateAmount(BigDecimal amount) throws InvalidTransactionAmountException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionAmountException("Amount must be greater than 0");
        }
    }

    private Account validateAccount(Long accountId) throws AccountNotExistException, AccountInactiveException, AccountFrozenException, AccountBannedException {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotExistException("Account not found"));
        validateAccountStatus(account);
        return account;
    }

    private void validateAccountStatus(Account account) throws AccountInactiveException, AccountFrozenException, AccountBannedException {
        switch (account.getAccountStatus()) {
            case INACTIVE -> throw new AccountInactiveException("Your account is INACTIVE!");
            case FROZEN -> throw new AccountFrozenException("Your account is frozen! You cannot perform any transactions!");
            case BANNED -> throw new AccountBannedException("Your account is BANNED!");
        }
    }

    private Transaction createTransaction(Account account, BigDecimal amount, TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setType(type);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAccount(account);
        return transaction;
    }

    private void sendTransactionSuccessEmail(Customer customer, TransactionRequest transactionRequest) throws UnknownTransactionTypeException, AccountNotExistException {
        EmailDetails emailToCustomer = new EmailDetails();
        emailToCustomer.setReceiver(customer.getEmail());

        TransactionType transactionType = transactionRequest.getType();
        emailToCustomer.setSubject(transactionType.toString());

        switch (transactionType) {
            case WITHDRAWAL:
                emailToCustomer.setBody(EmailUtils.sendEmailOnWithdrawal(customer, transactionRequest));
                emailService.sendEmail(emailToCustomer);
                break;
            case TRANSFER:
                emailToCustomer.setBody(EmailUtils.sendEmailOnTransfer(customer, transactionRequest));
                emailService.sendEmail(emailToCustomer);
                sendTransactionEmailToReceiver(transactionRequest);
                break;
            case DEPOSIT:
                emailToCustomer.setBody(EmailUtils.sendEmailOnDeposit(customer, transactionRequest));
                emailService.sendEmail(emailToCustomer);
                break;
            default:
                log.error("[timestamp:{}] Sent {} email to: {} : {}",
                        new Date(),
                        emailToCustomer.getSubject().toUpperCase(),
                        emailToCustomer.getReceiver(),
                        emailToCustomer.getBody()
                );
                throw new UnknownTransactionTypeException("Invalid transaction operation!" + transactionType);
        }
    }

    private void sendTransactionEmailToReceiver(TransactionRequest transactionRequest) throws AccountNotExistException {
        Customer receiver = accountRepository.findByAccountNumber(transactionRequest.getTransferToAccount())
                .orElseThrow(() -> new AccountNotExistException("Account does not exist: " + transactionRequest.getTransferToAccount()))
                .getAccountHolder();

        EmailDetails emailToReceiver = new EmailDetails();
        emailToReceiver.setReceiver(receiver.getEmail());
        emailToReceiver.setSubject("TRANSFER");
        emailToReceiver.setBody(EmailUtils.sendEmailOnReceiving(receiver, transactionRequest));

        emailService.sendEmail(emailToReceiver);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setId(transaction.getId());
        transactionResponse.setAmount(transaction.getAmount());
        transactionResponse.setTimestamp(transaction.getTimestamp());
        transactionResponse.setType(transaction.getType());
        transactionResponse.setStatus(transaction.getStatus());
        transactionResponse.setTransferToAccount(transaction.getTransferToAccount());
        return transactionResponse;
    }

    @Deprecated(
            since = "development XD",
            forRemoval = true
    )
    @SuppressWarnings("all")
    @Transactional(
            rollbackOn = Exception.class,
            dontRollbackOn = {MailException.class}
    )
    private TransactionResponse createTransaction(Long accountId, TransactionRequest transactionRequest) throws AccountNotExistException, InsufficientFundsException, InvalidTransactionAmountException, AccountInactiveException, AccountFrozenException, AccountBannedException {
        if (transactionRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionAmountException("Amount must be greater than 0");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotExistException("Account not found"));
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("You can not perform this action if your account is not active!");
        }
        switch (account.getAccountStatus()) {
            case INACTIVE -> throw new AccountInactiveException("You can not perform this action because your account is INACTIVE!");
            case FROZEN -> throw new AccountFrozenException("Your account is frozen! You can still log in but can not perform any transactions!");
            case BANNED -> throw new AccountBannedException("Your are banned!!!");
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setType(transactionRequest.getType());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAccount(account);

        if (transactionRequest.getType() == TransactionType.TRANSFER) {
            if (transactionRequest.getTransferToAccount() == null || transactionRequest.getTransferToAccount().isEmpty()) {
                throw new IllegalArgumentException("Transfer to account cannot be null or empty for TRANSFER transactions");
            }
            transaction.setTransferToAccount(transactionRequest.getTransferToAccount());
        }

        switch (transactionRequest.getType()) {
            case DEPOSIT:
                if (transactionRequest.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                    throw new InsufficientFundsException("Insufficient funds for deposit");
                }
                account.setBalance(account.getBalance().add(transactionRequest.getAmount()));
                transaction.setStatus(TransactionStatus.COMPLETED);
                break;
            case WITHDRAWAL:
                if (account.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
                    throw new InsufficientFundsException("Insufficient funds for withdrawal");
                }
                account.setBalance(account.getBalance().subtract(transactionRequest.getAmount()));
                transaction.setStatus(TransactionStatus.COMPLETED);
                break;
            case TRANSFER:
                if (account.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
                    throw new InsufficientFundsException("Insufficient funds for transfer");
                }
                Account transferToAccount = accountRepository.findByAccountNumber(transactionRequest.getTransferToAccount())
                        .orElseThrow(() -> new AccountNotExistException("Transfer to account not found"));

                switch (transferToAccount.getAccountStatus()) {
                    case INACTIVE -> throw new AccountInactiveException("This account is currently INACTIVE!");
                    case FROZEN -> throw new AccountFrozenException("This account is frozen! You can not perform any transactions with this account!");
                    case BANNED -> throw new AccountBannedException("The receiver are banned!!!");
                }

                account.setBalance(account.getBalance().subtract(transactionRequest.getAmount()));
                transferToAccount.setBalance(transferToAccount.getBalance().add(transactionRequest.getAmount()));
                accountRepository.save(transferToAccount);
                transaction.setStatus(TransactionStatus.COMPLETED);
                break;
        }

        accountRepository.save(account);
        transaction = transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }
}
