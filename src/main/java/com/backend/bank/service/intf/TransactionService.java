package com.backend.bank.service.intf;

import com.backend.bank.dto.request.TransactionRequest;
import com.backend.bank.dto.response.TransactionResponse;
import com.backend.bank.exception.*;

import jakarta.transaction.Transactional;

import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional(
        rollbackOn = Exception.class,
        dontRollbackOn = {MailException.class}
)
public interface TransactionService {
    List<TransactionResponse> getTransactionHistory(Long accountId, int page, int size) throws AccountNotExistException;

    TransactionResponse deposit(Long accountId, TransactionRequest transactionRequest) throws InvalidTransactionAmountException, AccountNotExistException, AccountInactiveException, AccountFrozenException, AccountBannedException, UnknownTransactionTypeException;

    TransactionResponse withdraw(Long accountId, TransactionRequest transactionRequest) throws AccountInactiveException, AccountNotExistException, AccountFrozenException, AccountBannedException, InvalidTransactionAmountException, InsufficientFundsException, UnknownTransactionTypeException;

    TransactionResponse transfer(Long accountId, TransactionRequest transactionRequest) throws InvalidTransactionAmountException, AccountNotExistException, AccountInactiveException, AccountFrozenException, AccountBannedException, InsufficientFundsException, UnknownTransactionTypeException;
}
