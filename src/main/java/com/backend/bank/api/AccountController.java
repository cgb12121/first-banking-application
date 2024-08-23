package com.backend.bank.api;

import com.backend.bank.dto.request.UpdateCustomerInfoRequest;
import com.backend.bank.dto.request.UpgradeAccountRequest;
import com.backend.bank.dto.response.UpdateCustomerInfoResponse;
import com.backend.bank.dto.response.UpgradeAccountResponse;
import com.backend.bank.service.intf.AccountService;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/account")
public class AccountController {

    AccountService accountService;

    @PatchMapping("/upgrade-account")
    public ResponseEntity<Map<String, Object>> updateAccount(
            @RequestBody @Valid UpgradeAccountRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult
                    .getFieldErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("errors", errors);

            return ResponseEntity.badRequest().body(response);
        }


        UpgradeAccountResponse upgradeResponse = accountService.upgradeAccount(request);
        return ResponseEntity.ok(createSuccessResponse(upgradeResponse));
    }

    @PatchMapping("/update-info")
    public ResponseEntity<Map<String, Object>> updateCustomerInfo(
            @RequestBody @Valid UpdateCustomerInfoRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult
                    .getFieldErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("errors", errors);

            return ResponseEntity.badRequest().body(response);
        }

        UpdateCustomerInfoResponse infoResponse = accountService.updateCustomerInfo(request);
        return ResponseEntity.ok(createSuccessResponse(infoResponse));

    }

    private Map<String, Object> createSuccessResponse(Object response) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", new Date());
        responseBody.put("status", HttpStatus.OK.value());
        responseBody.put("response", response);
        return responseBody;
    }

    @SuppressWarnings("all")
    private ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        Throwable cause = ex.getCause();
        return switch (cause.getClass().getSimpleName()) {
            case "AccountNotExistException" ->
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponse("Account does not exist."));
            case "AccountBannedException" ->
                    ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorResponse("Account is banned."));
            case "AccountInactiveException" ->
                    ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse("Account is in use."));
            default ->
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("An error occurred"));
        };
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", new Date());
        responseBody.put("message", message);
        return responseBody;
    }
}
