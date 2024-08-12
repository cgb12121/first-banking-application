package com.backend.bank.api;

import com.backend.bank.dto.request.LoanApplicationRequest;
import com.backend.bank.dto.request.LoanApprovalRequest;
import com.backend.bank.dto.request.LoanRepaymentRequest;
import com.backend.bank.dto.response.LoanApplicationResponse;
import com.backend.bank.dto.response.LoanDetailsResponse;
import com.backend.bank.dto.response.LoanRepaymentResponse;
import com.backend.bank.exception.*;
import com.backend.bank.service.intf.LoanService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/apply")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<LoanApplicationResponse> applyForLoan(@RequestBody LoanApplicationRequest request) throws CustomerNotFoundException {
        LoanApplicationResponse response = loanService.applyForLoan(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/approve")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF')")
    public ResponseEntity<LoanApplicationResponse> approveLoan(@RequestBody LoanApprovalRequest request) throws LoanNotFoundException, InvalidLoanStatusException {
        LoanApplicationResponse response = loanService.approveLoan(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/repay")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<LoanRepaymentResponse> makeRepayment(@RequestBody LoanRepaymentRequest request) throws LoanNotFoundException, InvalidRepaymentAmountException, InsufficientFundsException {
        LoanRepaymentResponse response = loanService.makeRepayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{loanId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF')")
    public ResponseEntity<LoanDetailsResponse> getLoanDetails(@PathVariable Long loanId) throws LoanNotFoundException {
        LoanDetailsResponse response = loanService.getLoanDetails(loanId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF')")
    public ResponseEntity<List<LoanDetailsResponse>> getCustomerLoans(@PathVariable Long customerId) throws CustomerNotFoundException {
        List<LoanDetailsResponse> response = loanService.getCustomerLoans(customerId);
        return ResponseEntity.ok(response);
    }
}
