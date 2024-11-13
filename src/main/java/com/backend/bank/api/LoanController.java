package com.backend.bank.api;

import com.backend.bank.dto.request.LoanApplicationRequest;
import com.backend.bank.dto.request.LoanApprovalRequest;
import com.backend.bank.dto.request.LoanRepaymentRequest;
import com.backend.bank.dto.response.LoanApplicationResponse;
import com.backend.bank.dto.response.LoanDetailsResponse;
import com.backend.bank.dto.response.LoanRepaymentResponse;
import com.backend.bank.service.intf.LoanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/loans")
@Tag(name = "Loan Management", description = "APIs for managing loan applications, approvals, and repayments")
public class LoanController {

    LoanService loanService;

    @Operation(
        summary = "Apply for a loan",
        description = "Submit a new loan application",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Loan application submitted successfully",
            content = @Content(schema = @Schema(implementation = LoanApplicationResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid loan application details"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Requires USER role"
        )
    })
    @PostMapping("/apply")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<LoanApplicationResponse> applyForLoan(
            @RequestBody @Valid 
            @Parameter(description = "Loan application details", required = true)
            LoanApplicationRequest request
    ) {
        LoanApplicationResponse response = this.loanService.applyForLoan(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Approve a loan",
        description = "Approve or reject a pending loan application",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Loan approval processed successfully",
            content = @Content(schema = @Schema(implementation = LoanApplicationResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid approval details"),
        @ApiResponse(responseCode = "403", description = "Access denied - Requires ADMIN/MANAGER/STAFF role")
    })
    @PostMapping("/approve")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF')")
    public ResponseEntity<LoanApplicationResponse> approveLoan(
            @RequestBody @Valid 
            @Parameter(description = "Loan approval details", required = true)
            LoanApprovalRequest request
    ) {
        LoanApplicationResponse response = this.loanService.approveLoan(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Make loan repayment",
        description = "Process a loan repayment",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Repayment processed successfully",
            content = @Content(schema = @Schema(implementation = LoanRepaymentResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid repayment details"),
        @ApiResponse(responseCode = "403", description = "Access denied - Requires USER role")
    })
    @PostMapping("/repay")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<LoanRepaymentResponse> makeRepayment(
            @RequestBody 
            @Parameter(description = "Loan repayment details", required = true)
            LoanRepaymentRequest request
    ) {
        LoanRepaymentResponse response = this.loanService.makeRepayment(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get loan details",
        description = "Retrieve details of a specific loan",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Loan details retrieved successfully",
            content = @Content(schema = @Schema(implementation = LoanDetailsResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Loan not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{loanId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF')")
    public ResponseEntity<LoanDetailsResponse> getLoanDetails(
            @Parameter(description = "ID of the loan", required = true)
            @PathVariable Long loanId
    ) {
        LoanDetailsResponse response = this.loanService.getLoanDetails(loanId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get customer loans",
        description = "Retrieve all loans for a specific customer",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Customer loans retrieved successfully",
            content = @Content(schema = @Schema(implementation = LoanDetailsResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF')")
    public ResponseEntity<List<LoanDetailsResponse>> getCustomerLoans(
            @Parameter(description = "ID of the customer", required = true)
            @PathVariable Long customerId
    ) {
        List<LoanDetailsResponse> response = this.loanService.getCustomerLoans(customerId);
        return ResponseEntity.ok(response);
    }
}
