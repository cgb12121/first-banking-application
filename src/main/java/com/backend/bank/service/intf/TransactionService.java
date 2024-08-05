package com.backend.bank.service.intf;

import com.backend.bank.dto.request.TransactionRequest;
import com.backend.bank.dto.response.TransactionResponse;
import com.backend.bank.exception.*;

import java.util.List;

public interface TransactionService {
    TransactionResponse createTransaction(Long accountId, TransactionRequest transactionRequest)
            throws AccountNotExistException, InsufficientFundsException, InvalidTransactionAmountException,
                    AccountInactiveException, AccountFrozenException, AccountBannedException;

    List<TransactionResponse> getTransactionHistory(Long accountId, int page, int size);
}
