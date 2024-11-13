package com.backend.bank.api;

import com.backend.bank.dto.request.UpdateCustomerInfoRequest;
import com.backend.bank.dto.request.UpgradeAccountRequest;
import com.backend.bank.service.intf.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/account")
@Tag(name = "Account Management", description = "APIs for managing bank accounts")
public class AccountController {

    AccountService accountService;

    @Operation(
        summary = "Upgrade account",
        description = "Upgrade a customer's account with new details"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Account upgraded successfully",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input provided",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @PatchMapping("/upgrade-account")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> updateAccount(
            @RequestBody @Valid @Parameter(description = "Account upgrade details", required = true) 
            UpgradeAccountRequest request,
            @RequestParam
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

        return accountService.upgradeAccount(request)
                .thenApply(upgradeAccountResponse -> ResponseEntity.ok().body(createSuccessResponse(upgradeAccountResponse)));
    }

    @Operation(
        summary = "Update customer information",
        description = "Update customer's personal information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Customer information updated successfully",
            content = @Content(schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input provided",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @PatchMapping("/update-info")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> updateCustomerInfo(
            @RequestBody @Valid 
            @Parameter(description = "Customer information details", required = true)
            UpdateCustomerInfoRequest request,
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

        return accountService.updateCustomerInfo(request)
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
