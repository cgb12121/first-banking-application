package com.backend.bank.api;

import com.backend.bank.dto.request.TransactionRequest;
import com.backend.bank.dto.response.TransactionResponse;
import com.backend.bank.security.SecurityWall;
import com.backend.bank.service.intf.TransactionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/transactions/")
public class TransactionController {

    TransactionService transactionService;

    @SuppressWarnings("unused")
    SecurityWall securityWall;

    @PostMapping("deposit/{accountId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<CompletableFuture<TransactionResponse>> deposit(
            @PathVariable(name = "accountId") Long accountId,
            @RequestBody @Valid TransactionRequest transactionRequest
    ) {
        CompletableFuture<TransactionResponse> response =
                this.transactionService.deposit(accountId, transactionRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("withdraw/{accountId}")
    @PreAuthorize("hasRole('USER') and @securityWall.canAccessAccount(#accountId)")
    public ResponseEntity<CompletableFuture<TransactionResponse>> withdraw(
            @PathVariable(name = "accountId") Long accountId,
            @RequestBody @Valid TransactionRequest transactionRequest
    ) {
        CompletableFuture<TransactionResponse> response =
                this.transactionService.withdraw(accountId, transactionRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("transfer{accountId}")
    @PreAuthorize("hasRole('USER') and @securityWall.canAccessAccount(#accountId)")
    public ResponseEntity<CompletableFuture<TransactionResponse>> transfer(
            @PathVariable(name = "accountId") @NotNull Long accountId,
            @RequestBody @Valid TransactionRequest transactionRequest
    ) {
        CompletableFuture<TransactionResponse> response =
                this.transactionService.transfer(accountId, transactionRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("history/all/{accountId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')" +
            " or (hasRole('USER') and @securityWall.canAccessAccount(#accountId))"
    )
    public ResponseEntity<CompletableFuture<List<TransactionResponse>>> getTransactionHistory(
            @PathVariable(name = "accountId") @NotNull Long accountId,
            @RequestParam(value = "0") int page,
            @RequestParam(value = "10") int size
    ) {
        CompletableFuture<List<TransactionResponse>> response =
                this.transactionService.getTransactionHistory(accountId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("history/deposit-transaction-history/{accountId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')" +
            " or (hasRole('USER') and @securityWall.canAccessAccount(#accountId))"
    )
    public ResponseEntity<CompletableFuture<List<TransactionResponse>>> getDepositTransactionHistory(
            @PathVariable(name = "accountId") @NotNull Long accountId,
            @RequestParam(value = "0") int page,
            @RequestParam(value = "10") int size
    ) {
        CompletableFuture<List<TransactionResponse>> response =
                this.transactionService.getDepositTransactionHistory(accountId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("history/withdraw-transaction-history/{accountId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')" +
            " or (hasRole('USER') and @securityWall.canAccessAccount(#accountId))"
    )
    public ResponseEntity<CompletableFuture<List<TransactionResponse>>> getWithdrawTransactionHistory(
            @PathVariable(name = "accountId") @NotNull Long accountId,
            @RequestParam(value = "0") int page,
            @RequestParam(value = "10") int size
    ) {
        CompletableFuture<List<TransactionResponse>> response =
                this.transactionService.getWithdrawTransactionHistory(accountId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("history/transferred-transaction-history/{accountId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')" +
            " or (hasRole('USER') and @securityWall.canAccessAccount(#accountId))"
    )
    public ResponseEntity<CompletableFuture<List<TransactionResponse>>> getTransferredTransactionHistory(
            @PathVariable(name = "accountId") @NotNull Long accountId,
            @RequestParam(value = "0") int page,
            @RequestParam(value = "10") int size
    ) {
        CompletableFuture<List<TransactionResponse>> response =
                this.transactionService.getSentTransactionHistory(accountId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("history/received-transaction-history/{accountId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')" +
            " or (hasRole('USER') and @securityWall.canAccessAccount(#accountId))"
    )
    public ResponseEntity<CompletableFuture<List<TransactionResponse>>> getReceivedTransactionHistory(
            @PathVariable(name = "accountId") @NotNull Long accountId,
            @RequestParam(value = "0") int page,
            @RequestParam(value = "10") int size
    ) {
        CompletableFuture<List<TransactionResponse>> response =
                this.transactionService.getReceivedTransactionHistory(accountId, page, size);
        return ResponseEntity.ok(response);
    }

}
