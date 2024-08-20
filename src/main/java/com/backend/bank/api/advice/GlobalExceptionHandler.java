package com.backend.bank.api.advice;

import com.backend.bank.exception.*;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleAccountAlreadyExistsException(
            AccountAlreadyExistsException e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.BAD_REQUEST);
        log.error("AccountAlreadyExistsException occurred: {}, {}, {} ",
                webRequest.getHeaderNames(), webRequest.getParameterMap(), errorDetails, e);
        return buildErrorResponse(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccountBannedException.class)
    public ResponseEntity<Map<String, Object>> handleAccountBannedException(
            AccountBannedException e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.FORBIDDEN);
        log.error("AccountBannedException occurred: {}, {}, {} ",
                webRequest.getHeaderNames(), webRequest.getParameterMap(), errorDetails, e);
        return buildErrorResponse(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccountFrozenException.class)
    public ResponseEntity<Map<String, Object>> handleAccountFrozenException(
            AccountFrozenException e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.FORBIDDEN);
        log.error("AccountFrozenException occurred: {}, {}, {} ",
                webRequest.getHeaderNames(), webRequest.getParameterMap(), errorDetails, e);
        return buildErrorResponse(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccountInactiveException.class)
    public ResponseEntity<Map<String, Object>> handleAccountInactiveException(
            AccountInactiveException e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.FORBIDDEN);
        log.error("AccountInactiveException occurred: {}, {}, {} ",
                webRequest.getHeaderNames(), webRequest.getParameterMap(), errorDetails, e);
        return buildErrorResponse(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccountNotExistException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotExistException(
            AccountNotExistException e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.UNAUTHORIZED);
        log.error("AccountNotExistException occurred: {}, {}, {} ",
                webRequest.getHeaderNames(), webRequest.getParameterMap(), errorDetails, e);
        return buildErrorResponse(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CantTransferToSelfException.class)
    public ResponseEntity<Map<String, Object>> handleCantTransferToSelfException(
            CantTransferToSelfException e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.FORBIDDEN);
        log.error("CantTransferToSelfException occurred: {}, {}, {} ",
                webRequest.getHeaderNames(), webRequest.getParameterMap(), errorDetails, e);
        return buildErrorResponse(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCustomerNotFoundException(
            CustomerNotFoundException e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.NOT_FOUND);
        log.error("CustomerNotFoundException occurred: {}, {}, {} ",
                webRequest.getHeaderNames(), webRequest.getParameterMap(), errorDetails, e);
        return buildErrorResponse(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InputViolationException.class)
    public ResponseEntity<Map<String, Object>> handleInputViolationException(
            InputViolationException e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.BAD_REQUEST);
        log.error("InputViolationException occurred: {}, {}, {} ",
                webRequest.getHeaderNames(), webRequest.getParameterMap(), errorDetails, e);
        return buildErrorResponse(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientFundsException(
            InsufficientFundsException e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.FORBIDDEN);
        log.error("InsufficientFundsException occurred: {}, {}, {} ",
                webRequest.getHeaderNames(), webRequest.getParameterMap(), errorDetails, e);
        return buildErrorResponse(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidLoanStatusException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidLoanStatusException(
            InvalidLoanStatusException e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.BAD_REQUEST);
        log.error("InvalidLoanStatusException occurred: {}, {}, {} ",
                webRequest.getHeaderNames(), webRequest.getParameterMap(), errorDetails, e);
        return buildErrorResponse(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRepaymentAmountException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRepaymentAmountException(
            InvalidRepaymentAmountException e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.BAD_REQUEST);
        log.error("InvalidRepaymentAmountException occurred: {}, {}, {} ",
                webRequest.getHeaderNames(), webRequest.getParameterMap(), errorDetails, e);
        return buildErrorResponse(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTokenException(
            InvalidTokenException e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.UNAUTHORIZED);
        log.error("InvalidTokenException occurred: {}, {}, {} ",
                webRequest.getHeaderNames(), webRequest.getParameterMap(), errorDetails, e);
        return buildErrorResponse(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidTransactionAmountException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTransactionAmountException(
            InvalidTransactionAmountException e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.UNAUTHORIZED);
        log.error("InvalidTransactionAmountException occurred: {}, {}, {} ",
                webRequest.getHeaderNames(), webRequest.getParameterMap(), errorDetails, e);
        return buildErrorResponse(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidVerifyLinkException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidVerifyLinkException(
            InvalidVerifyLinkException e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.UNAUTHORIZED);
        log.error("InvalidVerifyLinkException occurred: {}, {}, {} ",
                webRequest.getHeaderNames(), webRequest.getParameterMap(), errorDetails, e);
        return buildErrorResponse(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleLoanNotFoundException(
            LoanNotFoundException e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.NOT_FOUND);
        log.error("LoanNotFoundException occurred: {}, {}, {} ",
                webRequest.getHeaderNames(), webRequest.getParameterMap(), errorDetails, e);
        return buildErrorResponse(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleTokenExpiredException(
            TokenExpiredException e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.UNAUTHORIZED);
        log.error("TokenExpiredException occurred: {}, {}, {} ",
                webRequest.getHeaderNames(), webRequest.getParameterMap(), errorDetails, e);
        return buildErrorResponse(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnknownTransactionTypeException.class)
    public ResponseEntity<Map<String, Object>> handleUnknownTransactionTypeException(
            UnknownTransactionTypeException e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.BAD_REQUEST);
        log.error("UnknownTransactionTypeException occurred: {}, {}, {} ",
                webRequest.getHeaderNames(), webRequest.getParameterMap(), errorDetails, e);
        return buildErrorResponse(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(
            BadCredentialsException e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.UNAUTHORIZED);
        log.error("BadCredentialsException occurred: {}, {}, {} ",
                webRequest.getHeaderNames(), webRequest.getParameterMap(), errorDetails, e);
        return buildErrorResponse(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalAccountTypeException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalAccountTypeException(
            IllegalAccountTypeException e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.BAD_REQUEST);
        log.error("IllegalAccountTypeException occurred: {}, {}, {} ",
                webRequest.getHeaderNames(), webRequest.getParameterMap(), errorDetails, e);
        return buildErrorResponse(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, HttpServletRequest request, WebRequest webRequest
    ) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.BAD_REQUEST);
        loggingError(e, webRequest, errorDetails);
        return buildErrorResponse(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(
            Exception e, HttpServletRequest request, WebRequest webRequest) {
        Map<String, Object> errorDetails = buildErrorDetails(request, e, HttpStatus.INTERNAL_SERVER_ERROR);
        loggingError(e, webRequest, errorDetails);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }

    private Map<String, Object> buildErrorDetails(HttpServletRequest request, Exception e, HttpStatus status) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", Instant.now());
        errorDetails.put("status", status.value());
        errorDetails.put("error", e.getClass().getSimpleName());
        errorDetails.put("message", e.getMessage());
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("method", request.getMethod());
        errorDetails.put("user", request.getRemoteUser());
        return errorDetails;
    }

    private void loggingError(Exception e, WebRequest webRequest, Map<String, Object> errorDetails) {
        log.error("[TimeStamp: {}] {} occurred during request: {} [{} {}] by user [{}]: {}, {}",
                Instant.now(),
                e.getClass().getSimpleName(),
                webRequest.getClass().getSimpleName(),
                errorDetails.get("method"),
                errorDetails.get("path"),
                errorDetails.get("user"),
                errorDetails.get("message"),
                errorDetails);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(Map<String, Object> errorDetails, HttpStatus status) {
        return ResponseEntity.status(status).body(errorDetails);
    }
}
