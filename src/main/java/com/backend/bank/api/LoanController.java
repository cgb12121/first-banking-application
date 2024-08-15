package com.backend.bank.api;

import com.backend.bank.dto.request.LoanApplicationRequest;
import com.backend.bank.dto.request.LoanApprovalRequest;
import com.backend.bank.dto.request.LoanRepaymentRequest;
import com.backend.bank.dto.response.LoanApplicationResponse;
import com.backend.bank.dto.response.LoanDetailsResponse;
import com.backend.bank.dto.response.LoanRepaymentResponse;
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
    public ResponseEntity<LoanApplicationResponse> applyForLoan(@RequestBody LoanApplicationRequest request)  {
        LoanApplicationResponse response = this.loanService.applyForLoan(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/approve")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF')")
    public ResponseEntity<LoanApplicationResponse> approveLoan(@RequestBody LoanApprovalRequest request) {
        LoanApplicationResponse response = this.loanService.approveLoan(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/repay")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<LoanRepaymentResponse> makeRepayment(@RequestBody LoanRepaymentRequest request) {
        LoanRepaymentResponse response = this.loanService.makeRepayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{loanId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF')")
    public ResponseEntity<LoanDetailsResponse> getLoanDetails(@PathVariable Long loanId) {
        LoanDetailsResponse response = this.loanService.getLoanDetails(loanId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF')")
    public ResponseEntity<List<LoanDetailsResponse>> getCustomerLoans(@PathVariable Long customerId) {
        List<LoanDetailsResponse> response = this.loanService.getCustomerLoans(customerId);
        return ResponseEntity.ok(response);
    }
}
