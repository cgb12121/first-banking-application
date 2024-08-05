package com.backend.bank.service.impl;

import com.backend.bank.dto.request.TransactionRequest;
import com.backend.bank.dto.response.TransactionResponse;
import com.backend.bank.entity.Account;
import com.backend.bank.entity.Transaction;
import com.backend.bank.entity.constant.AccountStatus;
import com.backend.bank.entity.constant.TransactionStatus;
import com.backend.bank.entity.constant.TransactionType;
import com.backend.bank.exception.*;
import com.backend.bank.repository.AccountRepository;
import com.backend.bank.repository.TransactionRepository;
import com.backend.bank.service.intf.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    @Override
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

    @Override
    public List<TransactionResponse> getTransactionHistory(Long accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactionPage = transactionRepository.findByAccountId(accountId, pageable);
        return transactionPage.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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
}
