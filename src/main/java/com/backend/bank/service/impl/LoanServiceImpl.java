package com.backend.bank.service.impl;

import com.backend.bank.dto.request.LoanApplicationRequest;
import com.backend.bank.dto.request.LoanApprovalRequest;
import com.backend.bank.dto.request.LoanRepaymentRequest;
import com.backend.bank.dto.response.LoanApplicationResponse;
import com.backend.bank.dto.response.LoanDetailsResponse;
import com.backend.bank.dto.response.LoanRepaymentResponse;
import com.backend.bank.entity.Account;
import com.backend.bank.entity.Customer;
import com.backend.bank.entity.Loan;
import com.backend.bank.entity.constant.LoanStatus;
import com.backend.bank.entity.constant.TakeLoanStatus;
import com.backend.bank.exception.*;
import com.backend.bank.repository.AccountRepository;
import com.backend.bank.repository.CustomerRepository;
import com.backend.bank.repository.LoanRepository;
import com.backend.bank.service.intf.LoanService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;

    private final CustomerRepository customerRepository;

    private final AccountRepository accountRepository;

    @Override
    public LoanApplicationResponse applyForLoan(LoanApplicationRequest request) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        BigDecimal interestRateDecimal = request.interestRate().divide(BigDecimal.valueOf(100));
        BigDecimal interestAmount = request.amount().multiply(interestRateDecimal)
                .multiply(BigDecimal.valueOf(request.termInMonths())).divide(BigDecimal.valueOf(12), BigDecimal.ROUND_HALF_UP);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(request.termInMonths());

        Loan loan = Loan.builder()
                .amount(request.amount())
                .interestRate(request.interestRate())
                .interestAmount(interestAmount)
                .startDate(startDate)
                .endDate(endDate)
                .takeLoanStatus(TakeLoanStatus.PENDING)
                .loanStatus(LoanStatus.UNPAID)
                .customer(customer)
                .build();

        loan = loanRepository.save(loan);

        return mapToLoanApplicationResponse(loan, "Loan application submitted successfully and is pending approval.");
    }

    @Override
    public LoanApplicationResponse approveLoan(LoanApprovalRequest request) throws LoanNotFoundException, InvalidLoanStatusException {
        Loan loan = loanRepository.findById(request.loanId())
                .orElseThrow(() -> new LoanNotFoundException("Loan not found"));

        if (loan.getTakeLoanStatus() != TakeLoanStatus.PENDING) {
            throw new InvalidLoanStatusException("Loan is not in a pending state.");
        }

        if (request.approve()) {
            loan.setTakeLoanStatus(TakeLoanStatus.APPROVED);
            // Disburse loan amount to customer's account
            Account account = accountRepository.findByCustomerId(loan.getCustomer().getId())
                    .orElseThrow(() -> new AccountNotExistException("Account not found for customer."));
            account.setBalance(account.getBalance().add(loan.getAmount()));
            accountRepository.save(account);
            return mapToLoanApplicationResponse(loan, "Loan approved and amount disbursed to customer's account.");
        } else {
            loan.setTakeLoanStatus(TakeLoanStatus.REJECTED);
            return mapToLoanApplicationResponse(loan, "Loan application has been rejected.");
        }
    }

    @Override
    public LoanRepaymentResponse makeRepayment(LoanRepaymentRequest request) throws LoanNotFoundException, InvalidRepaymentAmountException, InsufficientFundsException {
        Loan loan = loanRepository.findById(request.loanId())
                .orElseThrow(() -> new LoanNotFoundException("Loan not found"));

        if (loan.getTakeLoanStatus() != TakeLoanStatus.APPROVED) {
            throw new InvalidRepaymentAmountException("Cannot make repayment on a loan that is not approved.");
        }

        BigDecimal totalOwed = loan.getAmount().add(loan.getInterestAmount());
        BigDecimal amountPaid = getTotalAmountPaid(loan);
        BigDecimal remainingAmount = totalOwed.subtract(amountPaid);

        if (request.repaymentAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRepaymentAmountException("Repayment amount must be greater than zero.");
        }

        if (request.repaymentAmount().compareTo(remainingAmount) > 0) {
            throw new InvalidRepaymentAmountException("Repayment amount exceeds the remaining loan amount.");
        }

        Account account = accountRepository.findByCustomerId(loan.getCustomer().getId())
                .orElseThrow(() -> new AccountNotExistException("Account not found for customer."));
        if (account.getBalance().compareTo(request.repaymentAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account for repayment.");
        }

        account.setBalance(account.getBalance().subtract(request.repaymentAmount()));
        accountRepository.save(account);

        // Update loan repayment status
        amountPaid = amountPaid.add(request.repaymentAmount());
        if (amountPaid.compareTo(totalOwed) >= 0) {
            loan.setLoanStatus(LoanStatus.PAID);
        }

        loanRepository.save(loan);

        return new LoanRepaymentResponse(
                loan.getId(),
                request.repaymentAmount(),
                totalOwed.subtract(amountPaid),
                LocalDateTime.now(),
                loan.getLoanStatus(),
                "Repayment successful."
        );
    }

    @Override
    public LoanDetailsResponse getLoanDetails(Long loanId) throws LoanNotFoundException {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found"));

        return mapToLoanDetailsResponse(loan);
    }

    @Override
    public List<LoanDetailsResponse> getCustomerLoans(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        List<Loan> loans = loanRepository.findByCustomerId(customer.getId());

        return loans.stream()
                .map(this::mapToLoanDetailsResponse)
                .collect(Collectors.toList());
    }

    private LoanApplicationResponse mapToLoanApplicationResponse(Loan loan, String message) {
        return new LoanApplicationResponse(
                loan.getId(),
                loan.getAmount(),
                loan.getInterestRate(),
                loan.getInterestAmount(),
                loan.getStartDate(),
                loan.getEndDate(),
                loan.getLoanStatus(),
                loan.getTakeLoanStatus(),
                message
        );
    }

    private LoanDetailsResponse mapToLoanDetailsResponse(Loan loan) {
        BigDecimal totalOwed = loan.getAmount().add(loan.getInterestAmount());
        BigDecimal amountPaid = getTotalAmountPaid(loan);
        BigDecimal remainingAmount = totalOwed.subtract(amountPaid);

        return new LoanDetailsResponse(
                loan.getId(),
                loan.getAmount(),
                loan.getInterestRate(),
                loan.getInterestAmount(),
                loan.getStartDate(),
                loan.getEndDate(),
                loan.getLoanStatus(),
                loan.getTakeLoanStatus(),
                remainingAmount
        );
    }

    private BigDecimal getTotalAmountPaid(Loan loan) {
        // Assuming you have a LoanRepayment entity/table to track repayments
        // For simplicity, we'll assume no repayments have been made
        // You can implement this method based on your actual repayment tracking
        return BigDecimal.ZERO;
    }
}
