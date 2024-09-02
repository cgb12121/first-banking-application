package com.backend.bank.api;

import com.backend.bank.dto.request.UpdateCustomerInfoRequest;
import com.backend.bank.dto.request.UpgradeAccountRequest;
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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/account")
public class AccountController {

    AccountService accountService;

    @PatchMapping("/upgrade-account")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> updateAccount(
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

            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(response));
        }

        return this.accountService.upgradeAccount(request)
                .thenApply(upgradeAccountResponse -> ResponseEntity.ok().body(createSuccessResponse(upgradeAccountResponse)));
    }

    @PatchMapping("/update-info")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> updateCustomerInfo(
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

            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(response));
        }

        return this.accountService.updateCustomerInfo(request)
                .thenApply(updateCustomerInfoResponse -> ResponseEntity.ok(createSuccessResponse(updateCustomerInfoResponse)));
    }

    private Map<String, Object> createSuccessResponse(Object response) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", new Date());
        responseBody.put("status", HttpStatus.OK.value());
        responseBody.put("response", response);
        return responseBody;
    }
}
