package com.backend.bank.service.intf;

import com.backend.bank.dto.request.TransactionRequest;
import com.backend.bank.dto.response.TransactionResponse;
import com.backend.bank.exception.*;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public interface TransactionService {

    /**
     * {@code Deposits} a specified amount into an account.
     *
     * @param accountId          The ID of the account.
     * @param transactionRequest The details of the deposit transaction.
     *
     * @return A {@link TransactionResponse} representing the completed transaction.
     *
     * @throws InvalidTransactionAmountException If the transaction amount is invalid.
     * @throws AccountNotExistException          If the account does not exist.
     * @throws AccountInactiveException          If the account is inactive.
     * @throws AccountFrozenException            If the account is frozen.
     * @throws AccountBannedException            If the account is banned.
     * @throws UnknownTransactionTypeException   If the transaction type is unknown.
     */
    CompletableFuture<TransactionResponse> deposit(Long accountId, TransactionRequest transactionRequest)
            throws InvalidTransactionAmountException, AccountNotExistException, AccountInactiveException,
            AccountFrozenException, AccountBannedException, UnknownTransactionTypeException;

    /**
     * {@code Withdraws} a specified amount from an account.
     *
     * @param accountId          The ID of the account.
     * @param transactionRequest The details of the withdrawal transaction.
     *
     * @return A {@link TransactionResponse} representing the completed transaction.
     *
     * @throws InvalidTransactionAmountException If the transaction amount is invalid.
     * @throws AccountNotExistException          If the account does not exist.
     * @throws AccountInactiveException          If the account is inactive.
     * @throws AccountFrozenException            If the account is frozen.
     * @throws AccountBannedException            If the account is banned.
     * @throws InsufficientFundsException        If there are insufficient funds for the withdrawal.
     * @throws UnknownTransactionTypeException   If the transaction type is unknown.
     */
    CompletableFuture<TransactionResponse> withdraw(Long accountId, TransactionRequest transactionRequest)
            throws AccountInactiveException, AccountNotExistException, AccountFrozenException,
            AccountBannedException, InvalidTransactionAmountException, InsufficientFundsException,
            UnknownTransactionTypeException;

    /**
     * {@code Transfers} a specified amount from one account to another.
     *
     * @param accountId          The ID of the sender's account.
     * @param transactionRequest The details of the transfer transaction.
     *
     * @return A {@link TransactionResponse} representing the completed transaction.
     *
     * @throws InvalidTransactionAmountException If the transaction amount is invalid.
     * @throws AccountNotExistException          If the sender or receiver account does not exist.
     * @throws AccountInactiveException          If the sender or receiver account is inactive.
     * @throws AccountFrozenException            If the sender or receiver account is frozen.
     * @throws AccountBannedException            If the sender or receiver account is banned.
     * @throws InsufficientFundsException        If there are insufficient funds for the transfer.
     * @throws UnknownTransactionTypeException   If the transaction type is unknown.
     * @throws CantTransferToSelfException       If the receiver is the same as the sender.
     */
    CompletableFuture<TransactionResponse> transfer(Long accountId, TransactionRequest transactionRequest)
            throws InvalidTransactionAmountException, AccountNotExistException, AccountInactiveException,
            AccountFrozenException, AccountBannedException, InsufficientFundsException,
            UnknownTransactionTypeException, CantTransferToSelfException;

    /**
     * {@code Retrieves} all the {@code transaction history} for a given account.
     *
     * @param accountId The ID of the account.
     * @param page      The page number for pagination.
     * @param size      The size of each page for pagination.
     *
     * @return A list of {@link TransactionResponse} representing the transaction history.
     *
     * @throws AccountNotExistException If the account does not exist.
     */
    CompletableFuture<List<TransactionResponse>> getTransactionHistory(Long accountId, int page, int size) throws AccountNotExistException;

    /**
     * {@code Retrieves} the {@code deposit transaction history} for a given account.
     *
     * @param accountId The ID of the account.
     * @param page      The page number for pagination.
     * @param size      The size of each page for pagination.
     *
     * @return A list of {@link TransactionResponse} representing the transaction history.
     *
     * @throws AccountNotExistException If the account does not exist.
     */
    CompletableFuture<List<TransactionResponse>> getDepositTransactionHistory(Long accountId, int page, int size) throws AccountNotExistException;

    /**
     * {@code Retrieves} the {@code withdrawal transaction history} for a given account.
     *
     * @param accountId The ID of the account.
     * @param page      The page number for pagination.
     * @param size      The size of each page for pagination.
     *
     * @return A list of {@link TransactionResponse} representing the transaction history.
     *
     * @throws AccountNotExistException If the account does not exist.
     */
    CompletableFuture<List<TransactionResponse>> getWithdrawTransactionHistory(Long accountId, int page, int size) throws AccountNotExistException;

    /**
     * {@code Retrieves} the {@code transferred transaction history} for a given account.
     *
     * @param accountId The ID of the account.
     * @param page      The page number for pagination.
     * @param size      The size of each page for pagination.
     *
     * @return A list of {@link TransactionResponse} representing the transaction history.
     *
     * @throws AccountNotExistException If the account does not exist.
     */
    CompletableFuture<List<TransactionResponse>> getSentTransactionHistory(Long accountId, int page, int size) throws AccountNotExistException;

    /**
     * {@code Retrieves} the {@code received transaction history} for a given account.
     *
     * @param accountId The ID of the account.
     * @param page      The page number for pagination.
     * @param size      The size of each page for pagination.
     *
     * @return A list of {@link TransactionResponse} representing the transaction history.
     *
     * @throws AccountNotExistException If the account does not exist.
     */
    CompletableFuture<List<TransactionResponse>> getReceivedTransactionHistory(Long accountId, int page, int size) throws AccountNotExistException;

    /**
     * {@code Add interest} to the users' account at the {@code first day of the month}
     *
     * @throws AccountNotExistException if the account does not exist.
     */
    @Scheduled
    void calculateInterest() throws AccountNotExistException;
}
