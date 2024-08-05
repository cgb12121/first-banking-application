package com.backend.bank.api;

import com.backend.bank.dto.request.TransactionRequest;
import com.backend.bank.dto.response.TransactionResponse;
import com.backend.bank.exception.*;
import com.backend.bank.service.intf.TransactionService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts/{accountId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/actions")
    public ResponseEntity<TransactionResponse> createTransaction(@PathVariable Long accountId, @RequestBody TransactionRequest transactionRequest) throws AccountNotExistException, InsufficientFundsException, InvalidTransactionAmountException, AccountFrozenException, AccountBannedException, AccountInactiveException {
        TransactionResponse response = transactionService.createTransaction(accountId, transactionRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<TransactionResponse> response = transactionService.getTransactionHistory(accountId, page, size);
        return ResponseEntity.ok(response);
    }
}
