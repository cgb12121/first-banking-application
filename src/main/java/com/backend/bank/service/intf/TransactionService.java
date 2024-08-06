package com.backend.bank.service.intf;

import com.backend.bank.dto.request.TransactionRequest;
import com.backend.bank.dto.response.TransactionResponse;
import com.backend.bank.exception.*;

import java.util.List;

public interface TransactionService {
    List<TransactionResponse> getTransactionHistory(Long accountId, int page, int size) throws AccountNotExistException;

    TransactionResponse deposit(Long accountId, TransactionRequest transactionRequest) throws InvalidTransactionAmountException, AccountNotExistException, AccountInactiveException, AccountFrozenException, AccountBannedException;

    TransactionResponse withdraw(Long accountId, TransactionRequest transactionRequest) throws AccountInactiveException, AccountNotExistException, AccountFrozenException, AccountBannedException, InvalidTransactionAmountException, InsufficientFundsException;

    TransactionResponse transfer(Long accountId, TransactionRequest transactionRequest) throws InvalidTransactionAmountException, AccountNotExistException, AccountInactiveException, AccountFrozenException, AccountBannedException, InsufficientFundsException;
}
