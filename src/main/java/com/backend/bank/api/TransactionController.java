package com.backend.bank.api;

import com.backend.bank.dto.request.TransactionRequest;
import com.backend.bank.dto.response.TransactionResponse;
import com.backend.bank.exception.*;
import com.backend.bank.service.intf.TransactionService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts/{accountId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF')")
    public ResponseEntity<TransactionResponse> deposit(
            @PathVariable Long accountId,
            @RequestBody TransactionRequest transactionRequest)
            throws AccountNotExistException, InvalidTransactionAmountException,
            AccountFrozenException, AccountBannedException, AccountInactiveException, UnknownTransactionTypeException {
        TransactionResponse response = transactionService.deposit(accountId, transactionRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('ROLE_USER') and @securityService.canAccessAccount(#accountId)")
    public ResponseEntity<TransactionResponse> withdraw(
            @PathVariable Long accountId,
            @RequestBody TransactionRequest transactionRequest)
            throws AccountNotExistException, InsufficientFundsException, InvalidTransactionAmountException,
            AccountFrozenException, AccountBannedException, AccountInactiveException, UnknownTransactionTypeException {
        TransactionResponse response = transactionService.withdraw(accountId, transactionRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('ROLE_USER') and @securityService.canAccessAccount(#accountId)")
    public ResponseEntity<TransactionResponse> transfer(
            @PathVariable Long accountId,
            @RequestBody TransactionRequest transactionRequest)
            throws AccountNotExistException, InsufficientFundsException, InvalidTransactionAmountException,
            AccountFrozenException, AccountBannedException, AccountInactiveException, UnknownTransactionTypeException {
        TransactionResponse response = transactionService.transfer(accountId, transactionRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    @PreAuthorize(
            "hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')" +
            " or (hasRole('ROLE_USER') and @securityService.canAccessAccount(#accountId))"
    )
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws AccountNotExistException {
        List<TransactionResponse> response = transactionService.getTransactionHistory(accountId, page, size);
        return ResponseEntity.ok(response);
    }

}
