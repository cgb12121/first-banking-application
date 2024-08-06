package com.backend.bank.service.intf;

import com.backend.bank.dto.request.TransactionRequest;
import com.backend.bank.dto.response.TransactionResponse;
import com.backend.bank.exception.*;
import com.backend.bank.service.impl.TransactionServiceImpl;

import jakarta.transaction.Transactional;

import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <pre>
 * Interface of the {@link TransactionServiceImpl}.
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
@Service
@Transactional(
        rollbackOn = Exception.class,
        dontRollbackOn = {MailException.class}
)
public interface TransactionService {
    /**
     * Retrieves the transaction history for a given account.
     *
     * @param accountId The ID of the account.
     * @param page The page number for pagination.
     * @param size The size of each page for pagination.
     * @return A list of {@link TransactionResponse} representing the transaction history.
     * @throws AccountNotExistException If the account does not exist.
     */
    List<TransactionResponse> getTransactionHistory(Long accountId, int page, int size) throws AccountNotExistException;

    /**
     * Deposits a specified amount into an account.
     *
     * @param accountId The ID of the account.
     * @param transactionRequest The details of the deposit transaction.
     * @return A {@link TransactionResponse} representing the completed transaction.
     * @throws InvalidTransactionAmountException If the transaction amount is invalid.
     * @throws AccountNotExistException If the account does not exist.
     * @throws AccountInactiveException If the account is inactive.
     * @throws AccountFrozenException If the account is frozen.
     * @throws AccountBannedException If the account is banned.
     * @throws UnknownTransactionTypeException If the transaction type is unknown.
     */
    TransactionResponse deposit(Long accountId, TransactionRequest transactionRequest) throws InvalidTransactionAmountException, AccountNotExistException, AccountInactiveException, AccountFrozenException, AccountBannedException, UnknownTransactionTypeException;

    /**
     * Withdraws a specified amount from an account.
     *
     * @param accountId The ID of the account.
     * @param transactionRequest The details of the withdrawal transaction.
     * @return A {@link TransactionResponse} representing the completed transaction.
     * @throws InvalidTransactionAmountException If the transaction amount is invalid.
     * @throws AccountNotExistException If the account does not exist.
     * @throws AccountInactiveException If the account is inactive.
     * @throws AccountFrozenException If the account is frozen.
     * @throws AccountBannedException If the account is banned.
     * @throws InsufficientFundsException If there are insufficient funds for the withdrawal.
     * @throws UnknownTransactionTypeException If the transaction type is unknown.
     */
    TransactionResponse withdraw(Long accountId, TransactionRequest transactionRequest) throws AccountInactiveException, AccountNotExistException, AccountFrozenException, AccountBannedException, InvalidTransactionAmountException, InsufficientFundsException, UnknownTransactionTypeException;

    /**
     * Transfers a specified amount from one account to another.
     *
     * @param accountId The ID of the sender's account.
     * @param transactionRequest The details of the transfer transaction.
     * @return A {@link TransactionResponse} representing the completed transaction.
     * @throws InvalidTransactionAmountException If the transaction amount is invalid.
     * @throws AccountNotExistException If the sender or receiver account does not exist.
     * @throws AccountInactiveException If the sender or receiver account is inactive.
     * @throws AccountFrozenException If the sender or receiver account is frozen.
     * @throws AccountBannedException If the sender or receiver account is banned.
     * @throws InsufficientFundsException If there are insufficient funds for the transfer.
     * @throws UnknownTransactionTypeException If the transaction type is unknown.
     * @throws CantTransferToSelfException If the receiver is the same as the sender.
     */
    TransactionResponse transfer(Long accountId, TransactionRequest transactionRequest) throws InvalidTransactionAmountException, AccountNotExistException, AccountInactiveException, AccountFrozenException, AccountBannedException, InsufficientFundsException, UnknownTransactionTypeException, CantTransferToSelfException;
}
