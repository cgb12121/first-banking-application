package com.backend.bank.service.intf;

import com.backend.bank.dto.request.LoanApplicationRequest;
import com.backend.bank.dto.request.LoanApprovalRequest;
import com.backend.bank.dto.request.LoanRepaymentRequest;
import com.backend.bank.dto.response.LoanApplicationResponse;
import com.backend.bank.dto.response.LoanDetailsResponse;
import com.backend.bank.dto.response.LoanRepaymentResponse;
import com.backend.bank.exception.*;

import java.util.List;

public interface LoanService {
    LoanApplicationResponse applyForLoan(LoanApplicationRequest request) throws CustomerNotFoundException;

    LoanApplicationResponse approveLoan(LoanApprovalRequest request) throws LoanNotFoundException, InvalidLoanStatusException, AccountNotExistException;

    LoanRepaymentResponse makeRepayment(LoanRepaymentRequest request) throws LoanNotFoundException, InvalidRepaymentAmountException, InsufficientFundsException, AccountNotExistException;

    LoanDetailsResponse getLoanDetails(Long loanId) throws LoanNotFoundException;

    List<LoanDetailsResponse> getCustomerLoans(Long customerId) throws CustomerNotFoundException;
}
