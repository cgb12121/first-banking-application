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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <pre>
 * Implementation of the {@link TransactionService} interface.
 * Provides methods for handling transactions:
 * </pre>
 *
 * <dl>
 *   <dt>{@code deposit(Long accountId, TransactionRequest transactionRequest)}</dt>
 *   <dd>Perform deposit of money into an account.</dd>
 *   <dd>&nbsp;</dd>
 *
 *   <dt>{@code withdraw(Long accountId, TransactionRequest transactionRequest)}</dt>
 *   <dd>Perform withdrawal of money from an account.</dd>
 *   <dd>&nbsp;</dd>
 *
 *   <dt>{@code transfer(Long accountId, TransactionRequest transactionRequest)}</dt>
 *   <dd>Perform transfer of money between accounts.</dd>
 *   <dd>&nbsp;</dd>
 *
 *   <dt>{@code getTransactionHistory(Long accountId, int page, int size)}</dt>
 *   <dd>Fetch the transaction history of a user's account.</dd>
 * </dl>
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    private final EmailService emailService;

    /**
     * {@code Deposits} a specified amount into an account.
     *
     * @param accountId          The ID of the account.
     * @param transactionRequest The details of the deposit transaction.
     * @return A {@link TransactionResponse} representing the completed transaction.
     * @throws InvalidTransactionAmountException If the transaction amount is invalid.
     * @throws AccountNotExistException          If the account does not exist.
     * @throws AccountInactiveException          If the account is inactive.
     * @throws AccountFrozenException            If the account is frozen.
     * @throws AccountBannedException            If the account is banned.
     * @throws UnknownTransactionTypeException   If the transaction type is unknown.
     */
    @Override
    @Transactional(
            rollbackOn = Exception.class,
            dontRollbackOn = {MailException.class}
    )
    @Async(value = "transactionTaskExecutor")
    public CompletableFuture<TransactionResponse> deposit(Long accountId, TransactionRequest transactionRequest) throws InvalidTransactionAmountException, AccountNotExistException, AccountInactiveException, AccountFrozenException, AccountBannedException, UnknownTransactionTypeException {
        validateAmount(transactionRequest.getAmount());
        Account account = validateAccount(accountId);

        Transaction transaction = createTransaction(account, transactionRequest.getAmount(), TransactionType.DEPOSIT);

        account.setBalance(account.getBalance().add(transactionRequest.getAmount()));
        accountRepository.save(account);

        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction = transactionRepository.save(transaction);

        sendTransactionSuccessEmail(account.getAccountHolder(), transactionRequest);

        return CompletableFuture.completedFuture(mapToResponse(transaction));
    }

    /**
     * {@code Withdraws} a specified amount from an account.
     *
     * @param accountId          The ID of the account.
     * @param transactionRequest The details of the withdrawal transaction.
     * @return A {@link TransactionResponse} representing the completed transaction.
     * @throws InvalidTransactionAmountException If the transaction amount is invalid.
     * @throws AccountNotExistException          If the account does not exist.
     * @throws AccountInactiveException          If the account is inactive.
     * @throws AccountFrozenException            If the account is frozen.
     * @throws AccountBannedException            If the account is banned.
     * @throws InsufficientFundsException        If there are insufficient funds for the withdrawal.
     * @throws UnknownTransactionTypeException   If the transaction type is unknown.
     */
    @Override
    @Transactional(
            rollbackOn = Exception.class,
            dontRollbackOn = {MailException.class}
    )
    @Async(value = "transactionTaskExecutor")
    public CompletableFuture<TransactionResponse> withdraw(Long accountId, TransactionRequest transactionRequest) throws InvalidTransactionAmountException, AccountNotExistException, AccountInactiveException, AccountFrozenException, AccountBannedException, InsufficientFundsException, UnknownTransactionTypeException {
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

        return CompletableFuture.completedFuture(mapToResponse(transaction));
    }

    /**
     * {@code Transfers} a specified amount from one account to another.
     *
     * @param accountId          The ID of the sender's account.
     * @param transactionRequest The details of the transfer transaction.
     * @return A {@link TransactionResponse} representing the completed transaction.
     * @throws InvalidTransactionAmountException If the transaction amount is invalid.
     * @throws AccountNotExistException          If the sender or receiver account does not exist.
     * @throws AccountInactiveException          If the sender or receiver account is inactive.
     * @throws AccountFrozenException            If the sender or receiver account is frozen.
     * @throws AccountBannedException            If the sender or receiver account is banned.
     * @throws InsufficientFundsException        If there are insufficient funds for the transfer.
     * @throws UnknownTransactionTypeException   If the transaction type is unknown.
     * @throws CantTransferToSelfException       If the receiver is the same as the sender.
     */
    @Override
    @Transactional(
            rollbackOn = Exception.class,
            dontRollbackOn = {MailException.class}
    )
    @Async(value = "transactionTaskExecutor")
    public CompletableFuture<TransactionResponse> transfer(Long accountId, TransactionRequest transactionRequest) throws InvalidTransactionAmountException, AccountNotExistException, AccountInactiveException, AccountFrozenException, AccountBannedException, InsufficientFundsException, UnknownTransactionTypeException, CantTransferToSelfException {
        validateAmount(transactionRequest.getAmount());
        Account account = validateAccount(accountId);

        if (account.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds for transfer");
        }

        Account transferToAccount = accountRepository.findByAccountNumber(transactionRequest.getTransferToAccount())
                .orElseThrow(() -> new AccountNotExistException("Transfer to account not found"));

        if (account.equals(transferToAccount)) {
            throw new CantTransferToSelfException("You can't transfer to yourself!");
        }

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

        return CompletableFuture.completedFuture(mapToResponse(transaction));
    }

    /**
     * {@code Retrieves} all the {@code transaction history} for a given account.
     *
     * @param accountId The ID of the account.
     * @param page      The page number for pagination.
     * @param size      The size of each page for pagination.
     * @return A list of {@link TransactionResponse} representing the transaction history.
     * @throws AccountNotExistException If the account does not exist.
     */
    @Override
    @Async(value = "transactionTaskExecutor")
    public CompletableFuture<List<TransactionResponse>> getTransactionHistory(Long accountId, int page, int size) throws AccountNotExistException {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotExistException("Account not found"));
        String accountNumber = account.getAccountNumber();

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> sentTransactions = transactionRepository.findByAccount_AccountNumber(accountNumber, pageable);
        Page<Transaction> receivedTransactions = transactionRepository.findByTransferToAccount(accountNumber, pageable);

        return CompletableFuture.completedFuture(Stream.concat(sentTransactions.stream(), receivedTransactions.stream())
                .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    /**
     * {@code Retrieves} the {@code deposit transaction history} for a given account.
     *
     * @param accountId The ID of the account.
     * @param page      The page number for pagination.
     * @param size      The size of each page for pagination.
     * @return A list of {@link TransactionResponse} representing the transaction history.
     * @throws AccountNotExistException If the account does not exist.
     */
    @Override
    @Async(value = "transactionTaskExecutor")
    public CompletableFuture<List<TransactionResponse>> getDepositTransactionHistory(Long accountId, int page, int size) throws AccountNotExistException {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotExistException("Account not found"));
        String accountNumber = account.getAccountNumber();

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findByAccount_AccountNumber(accountNumber, pageable);

        return CompletableFuture.completedFuture(transactions.stream()
                .filter(transaction -> transaction.getType() == TransactionType.DEPOSIT)
                .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
                .map(this::mapToResponse).collect(Collectors.toList()));
    }

    /**
     * {@code Retrieves} the {@code withdrawal transaction history} for a given account.
     *
     * @param accountId The ID of the account.
     * @param page      The page number for pagination.
     * @param size      The size of each page for pagination.
     * @return A list of {@link TransactionResponse} representing the transaction history.
     * @throws AccountNotExistException If the account does not exist.
     */
    @Override
    @Async(value = "transactionTaskExecutor")
    public CompletableFuture<List<TransactionResponse>> getWithdrawTransactionHistory(Long accountId, int page, int size) throws AccountNotExistException {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotExistException("Account not found"));
        String accountNumber = account.getAccountNumber();

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findByAccount_AccountNumber(accountNumber, pageable);

        return CompletableFuture.completedFuture(transactions.stream()
                .filter(transaction -> transaction.getType() == TransactionType.WITHDRAWAL)
                .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
                .map(this::mapToResponse).collect(Collectors.toList()));
    }

    /**
     * {@code Retrieves} the {@code transferred transaction history} for a given account.
     *
     * @param accountId The ID of the account.
     * @param page      The page number for pagination.
     * @param size      The size of each page for pagination.
     * @return A list of {@link TransactionResponse} representing the transaction history.
     * @throws AccountNotExistException If the account does not exist.
     */
    @Override
    @Async(value = "transactionTaskExecutor")
    public CompletableFuture<List<TransactionResponse>> getSentTransactionHistory(Long accountId, int page, int size) throws AccountNotExistException {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotExistException("Account not found"));
        String accountNumber = account.getAccountNumber();

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findByAccount_AccountNumber(accountNumber, pageable);

        return CompletableFuture.completedFuture(transactions.stream()
                .filter(transaction -> transaction.getType() == TransactionType.TRANSFER)
                .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
                .map(this::mapToResponse).collect(Collectors.toList()));
    }

    /**
     * {@code Retrieves} the {@code received transaction history} for a given account.
     *
     * @param accountId The ID of the account.
     * @param page      The page number for pagination.
     * @param size      The size of each page for pagination.
     * @return A list of {@link TransactionResponse} representing the transaction history.
     * @throws AccountNotExistException If the account does not exist.
     */
    @Override
    @Async(value = "transactionTaskExecutor")
    public CompletableFuture<List<TransactionResponse>> getReceivedTransactionHistory(Long accountId, int page, int size) throws AccountNotExistException {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotExistException("Account not found"));
        String accountNumber = account.getAccountNumber();

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findAllByTransferToAccount(accountNumber, pageable);

        return CompletableFuture.completedFuture(transactions.stream()
                .filter(transaction -> transaction.getType() == TransactionType.TRANSFER)
                .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
                .map(this::mapToResponse).collect(Collectors.toList()));
    }

    /**
     * {@code Validates} the {@code transaction amount}.
     *
     * @param amount The transaction amount to be validated.
     * @throws InvalidTransactionAmountException If the amount is less than or equal to zero.
     */
    private void validateAmount(BigDecimal amount) throws InvalidTransactionAmountException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionAmountException("Amount must be greater than 0");
        }
    }

    /**
     * {@code Validates} the {@code account status}.
     *
     * @param accountId The ID of the account to be validated.
     * @return The validated {@link Account}.
     * @throws AccountNotExistException If the account does not exist.
     * @throws AccountInactiveException If the account is inactive.
     * @throws AccountFrozenException If the account is frozen.
     * @throws AccountBannedException If the account is banned.
     */
    private Account validateAccount(Long accountId) throws AccountNotExistException, AccountInactiveException, AccountFrozenException, AccountBannedException {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotExistException("Account not found"));
        validateAccountStatus(account);
        return account;
    }

    /**
     * {@code Validates} the {@code status of an account}.
     *
     * @param account The account to be validated.
     * @throws AccountInactiveException If the account is inactive.
     * @throws AccountFrozenException If the account is frozen.
     * @throws AccountBannedException If the account is banned.
     */
    private void validateAccountStatus(Account account) throws AccountInactiveException, AccountFrozenException, AccountBannedException {
        switch (account.getAccountStatus()) {
            case INACTIVE -> throw new AccountInactiveException("Your account is INACTIVE!");
            case FROZEN -> throw new AccountFrozenException("Your account is frozen! You cannot perform any transactions!");
            case BANNED -> throw new AccountBannedException("Your account is BANNED!");
        }
    }

    /**
     * {@code Creates} a new {@code transaction}.
     *
     * @param account The account associated with the transaction.
     * @param amount The amount of the transaction.
     * @param type The type of the transaction.
     * @return The created {@link Transaction}.
     */
    private Transaction createTransaction(Account account, BigDecimal amount, TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setType(type);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAccount(account);
        return transaction;
    }

    /**
     * {@code Sends} a success {@code email} for the {@code transaction}.
     *
     * @param customer The customer associated with the transaction.
     * @param transactionRequest The transaction request details.
     * @throws UnknownTransactionTypeException If the transaction type is unknown.
     * @throws AccountNotExistException If the account does not exist.
     */
    private void sendTransactionSuccessEmail(Customer customer, TransactionRequest transactionRequest) throws UnknownTransactionTypeException, AccountNotExistException {
        EmailDetails emailToCustomer = new EmailDetails();
        emailToCustomer.setReceiver(customer.getEmail());

        TransactionType transactionType = transactionRequest.getType();
        emailToCustomer.setSubject(transactionType.toString());

        switch (transactionType) {
            case WITHDRAWAL:
                emailToCustomer.setBody(EmailUtils.sendEmailOnWithdrawal(customer, transactionRequest, new Date()));
                emailService.sendEmail(emailToCustomer);
                break;
            case TRANSFER:
                Date transferDate = new Date();
                emailToCustomer.setBody(EmailUtils.sendEmailOnTransfer(customer, transactionRequest, transferDate));
                emailService.sendEmail(emailToCustomer);
                sendTransactionEmailToReceiver(transactionRequest, transferDate);
                break;
            case DEPOSIT:
                emailToCustomer.setBody(EmailUtils.sendEmailOnDeposit(customer, transactionRequest, new Date()));
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

    /**
     * Sends an {@code email notification} to the receiver of a transfer.
     *
     * @param transactionRequest The transaction request details.
     * @throws AccountNotExistException If the receiver account does not exist.
     */
    private void sendTransactionEmailToReceiver(TransactionRequest transactionRequest, Date receivedDate) throws AccountNotExistException {
        Customer receiver = accountRepository.findByAccountNumber(transactionRequest.getTransferToAccount())
                .orElseThrow(() -> new AccountNotExistException("Account does not exist: " + transactionRequest.getTransferToAccount()))
                .getAccountHolder();

        EmailDetails emailToReceiver = new EmailDetails();
        emailToReceiver.setReceiver(receiver.getEmail());
        emailToReceiver.setSubject("TRANSFER");
        emailToReceiver.setBody(EmailUtils.sendEmailOnReceiving(receiver, transactionRequest, receivedDate));

        emailService.sendEmail(emailToReceiver);
    }

    /**
     * Maps a {@link Transaction} entity to a {@link TransactionResponse}.
     *
     * @param transaction The transaction entity.
     * @return The transaction response.
     */
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

    /**
     * {@code @Deprecated}
     * This method laid here for no reason. It will never be deleted.
     */
    @Deprecated(
            since = "development XD",
            forRemoval = true
    )
    @Transactional(
            rollbackOn = Exception.class,
            dontRollbackOn = {MailException.class}
    )
    public TransactionResponse createTransaction(Long accountId, TransactionRequest transactionRequest) throws AccountNotExistException, InsufficientFundsException, InvalidTransactionAmountException, AccountInactiveException, AccountFrozenException, AccountBannedException {
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
