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
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts/{accountId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF')")
    public ResponseEntity<CompletableFuture<TransactionResponse>> deposit(
            @PathVariable Long accountId,
            @RequestBody TransactionRequest transactionRequest
    ) throws AccountNotExistException, InvalidTransactionAmountException, AccountFrozenException,
            AccountBannedException, AccountInactiveException, UnknownTransactionTypeException {
        CompletableFuture<TransactionResponse> response =
                transactionService.deposit(accountId, transactionRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('ROLE_USER') and @securityService.canAccessAccount(#accountId)")
    public ResponseEntity<CompletableFuture<TransactionResponse>> withdraw(
            @PathVariable Long accountId,
            @RequestBody TransactionRequest transactionRequest
    ) throws AccountNotExistException, InsufficientFundsException, InvalidTransactionAmountException,
            AccountFrozenException, AccountBannedException, AccountInactiveException, UnknownTransactionTypeException {
        CompletableFuture<TransactionResponse> response =
                transactionService.withdraw(accountId, transactionRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('ROLE_USER') and @securityService.canAccessAccount(#accountId)")
    public ResponseEntity<CompletableFuture<TransactionResponse>> transfer(
            @PathVariable Long accountId,
            @RequestBody TransactionRequest transactionRequest
    ) throws AccountNotExistException, InsufficientFundsException, InvalidTransactionAmountException, AccountFrozenException,
            AccountBannedException, AccountInactiveException, UnknownTransactionTypeException, CantTransferToSelfException {
        CompletableFuture<TransactionResponse> response =
                transactionService.transfer(accountId, transactionRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/all")
    @PreAuthorize(
            "hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')" +
            " or (hasRole('ROLE_USER') and @securityService.canAccessAccount(#accountId))"
    )
    public ResponseEntity<CompletableFuture<List<TransactionResponse>>> getTransactionHistory(
            @PathVariable Long accountId,
            @RequestParam(value = "0") int page,
            @RequestParam(value = "10") int size
    ) throws AccountNotExistException {
        CompletableFuture<List<TransactionResponse>> response =
                transactionService.getTransactionHistory(accountId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/deposit")
    @PreAuthorize(
            "hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')" +
            " or (hasRole('ROLE_USER') and @securityService.canAccessAccount(#accountId))"
    )
    public ResponseEntity<CompletableFuture<List<TransactionResponse>>> getDepositTransactionHistory(
            @PathVariable Long accountId,
            @RequestParam(value = "0") int page,
            @RequestParam(value = "10") int size
    ) throws AccountNotExistException {
        CompletableFuture<List<TransactionResponse>> response =
                transactionService.getDepositTransactionHistory(accountId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/deposit")
    @PreAuthorize(
            "hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')" +
            " or (hasRole('ROLE_USER') and @securityService.canAccessAccount(#accountId))"
    )
    public ResponseEntity<CompletableFuture<List<TransactionResponse>>> getWithdrawTransactionHistory(
            @PathVariable Long accountId,
            @RequestParam(value = "0") int page,
            @RequestParam(value = "10") int size
    ) throws AccountNotExistException {
        CompletableFuture<List<TransactionResponse>> response =
                transactionService.getWithdrawTransactionHistory(accountId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/deposit")
    @PreAuthorize(
            "hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')" +
            " or (hasRole('ROLE_USER') and @securityService.canAccessAccount(#accountId))"
    )
    public ResponseEntity<CompletableFuture<List<TransactionResponse>>> getTransferredTransactionHistory(
            @PathVariable Long accountId,
            @RequestParam(value = "0") int page,
            @RequestParam(value = "10") int size
    ) throws AccountNotExistException {
        CompletableFuture<List<TransactionResponse>> response =
                transactionService.getSentTransactionHistory(accountId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/deposit")
    @PreAuthorize(
            "hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')" +
            " or (hasRole('ROLE_USER') and @securityService.canAccessAccount(#accountId))"
    )
    public ResponseEntity<CompletableFuture<List<TransactionResponse>>> getReceivedTransactionHistory(
            @PathVariable Long accountId,
            @RequestParam(value = "0") int page,
            @RequestParam(value = "10") int size
    ) throws AccountNotExistException {
        CompletableFuture<List<TransactionResponse>> response =
                transactionService.getReceivedTransactionHistory(accountId, page, size);
        return ResponseEntity.ok(response);
    }

}
