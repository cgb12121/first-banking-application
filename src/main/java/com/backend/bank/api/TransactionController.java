package com.backend.bank.api;

import com.backend.bank.dto.request.TransactionRequest;
import com.backend.bank.dto.response.TransactionResponse;
import com.backend.bank.security.SecurityWall;
import com.backend.bank.service.intf.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

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

@CrossOrigin
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/transactions/{accountId}/")
@Tag(name = "Transaction Management", description = "APIs for managing bank transactions including deposits, withdrawals, transfers, and transaction history")
public class TransactionController {

    TransactionService transactionService;

    @SuppressWarnings("unused")
    SecurityWall securityWall;

    @Operation(
        summary = "Deposit money",
        description = "Deposit money into a bank account (Staff only)",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Deposit successful",
            content = @Content(schema = @Schema(implementation = TransactionResponse.class))
        ),
        @ApiResponse(responseCode = "403", description = "Access denied - Requires ADMIN/MANAGER/STAFF role"),
        @ApiResponse(responseCode = "400", description = "Invalid transaction details")
    })
    @PostMapping("/deposit")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF')")
    public ResponseEntity<CompletableFuture<TransactionResponse>> deposit(
            @Parameter(description = "Account ID", required = true)
            @PathVariable(name = "accountId") Long accountId,
            @Parameter(description = "Transaction details", required = true)
            @RequestBody @Valid TransactionRequest transactionRequest
    ) {
        CompletableFuture<TransactionResponse> response = transactionService.deposit(accountId, transactionRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Withdraw money",
        description = "Withdraw money from a bank account",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Withdrawal successful",
            content = @Content(schema = @Schema(implementation = TransactionResponse.class))
        ),
        @ApiResponse(responseCode = "403", description = "Access denied or insufficient account access"),
        @ApiResponse(responseCode = "400", description = "Invalid transaction details or insufficient funds")
    })
    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('ROLE_USER') and @securityWall.canAccessAccount(#accountId)")
    public ResponseEntity<CompletableFuture<TransactionResponse>> withdraw(
            @PathVariable(name = "accountId") Long accountId,
            @RequestBody @Valid TransactionRequest transactionRequest
    ) {
        CompletableFuture<TransactionResponse> response = transactionService.withdraw(accountId, transactionRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Transfer money",
        description = "Transfer money between bank accounts",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Transfer successful",
            content = @Content(schema = @Schema(implementation = TransactionResponse.class))
        ),
        @ApiResponse(responseCode = "403", description = "Access denied or insufficient account access"),
        @ApiResponse(responseCode = "400", description = "Invalid transaction details or insufficient funds")
    })
    @PostMapping("/transfer")
    @PreAuthorize("hasRole('ROLE_USER') and @securityWall.canAccessAccount(#accountId)")
    public ResponseEntity<CompletableFuture<TransactionResponse>> transfer(
            @PathVariable(name = "accountId") @NotNull Long accountId,
            @RequestBody @Valid TransactionRequest transactionRequest
    ) {
        CompletableFuture<TransactionResponse> response = transactionService.transfer(accountId, transactionRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get all transaction history",
        description = "Retrieve complete transaction history for an account",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Transaction history retrieved successfully",
            content = @Content(schema = @Schema(implementation = TransactionResponse.class))
        ),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/history/all")
    @PreAuthorize(
            "hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')" +
            " or (hasRole('ROLE_USER') and @securityWall.canAccessAccount(#accountId))"
    )
    public ResponseEntity<CompletableFuture<List<TransactionResponse>>> getTransactionHistory(
            @Parameter(description = "Account ID", required = true)
            @PathVariable(name = "accountId") @NotNull Long accountId,
            @Parameter(description = "Page number (starts from 0)", example = "0")
            @RequestParam(value = "0") int page,
            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(value = "10") int size
    ) {
        CompletableFuture<List<TransactionResponse>> response = transactionService.getTransactionHistory(accountId, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get deposit history",
        description = "Retrieve deposit transaction history"
    )
    @GetMapping("/history/deposit-transaction-history")
    @PreAuthorize(
            "hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')" +
            " or (hasRole('ROLE_USER') and @securityWall.canAccessAccount(#accountId))"
    )
    public ResponseEntity<CompletableFuture<List<TransactionResponse>>> getDepositTransactionHistory(
            @PathVariable(name = "accountId") @NotNull Long accountId,
            @RequestParam(value = "0") int page,
            @RequestParam(value = "10") int size
    ) {
        CompletableFuture<List<TransactionResponse>> response = transactionService.getDepositTransactionHistory(accountId, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get withdrawal history",
        description = "Retrieve withdrawal transaction history"
    )
    @GetMapping("/history/withdraw-transaction-history")
    @PreAuthorize(
            "hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')" +
            " or (hasRole('ROLE_USER') and @securityWall.canAccessAccount(#accountId))"
    )
    public ResponseEntity<CompletableFuture<List<TransactionResponse>>> getWithdrawTransactionHistory(
            @PathVariable(name = "accountId") @NotNull Long accountId,
            @RequestParam(value = "0") int page,
            @RequestParam(value = "10") int size
    ) {
        CompletableFuture<List<TransactionResponse>> response = transactionService.getWithdrawTransactionHistory(accountId, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get transferred money history",
        description = "Retrieve history of money transferred to other accounts"
    )
    @GetMapping("/history/transferred-transaction-history")
    @PreAuthorize(
            "hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')" +
            " or (hasRole('ROLE_USER') and @securityWall.canAccessAccount(#accountId))"
    )
    public ResponseEntity<CompletableFuture<List<TransactionResponse>>> getTransferredTransactionHistory(
            @PathVariable(name = "accountId") @NotNull Long accountId,
            @RequestParam(value = "0") int page,
            @RequestParam(value = "10") int size
    ) {
        CompletableFuture<List<TransactionResponse>> response = transactionService.getSentTransactionHistory(accountId, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get received money history",
        description = "Retrieve history of money received from other accounts"
    )
    @GetMapping("/history/received-transaction-history")
    @PreAuthorize(
            "hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')" +
            " or (hasRole('ROLE_USER') and @securityWall.canAccessAccount(#accountId))"
    )
    public ResponseEntity<CompletableFuture<List<TransactionResponse>>> getReceivedTransactionHistory(
            @PathVariable(name = "accountId") @NotNull Long accountId,
            @RequestParam(value = "0") int page,
            @RequestParam(value = "10") int size
    ) {
        CompletableFuture<List<TransactionResponse>> response = transactionService.getReceivedTransactionHistory(accountId, page, size);
        return ResponseEntity.ok(response);
    }

}
