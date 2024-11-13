package com.backend.bank.api;

import com.backend.bank.dto.request.ChangeEmailRequest;
import com.backend.bank.dto.response.ChangeEmailResponse;
import com.backend.bank.service.intf.CustomerService;

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

import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/customer/email")
@Tag(name = "Email Management", description = "APIs for managing customer email addresses")
public class EmailController {

    CustomerService customerService;

    @Operation(
        summary = "Change email address",
        description = "Request to change customer's email address. Sends confirmation email to new address."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Email change request processed successfully",
            content = @Content(schema = @Schema(implementation = ChangeEmailResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid email address or request",
            content = @Content(schema = @Schema(implementation = ChangeEmailResponse.class))
        )
    })
    @PostMapping("/change")
    public ChangeEmailResponse changeEmail(
            @RequestBody @Valid 
            @Parameter(description = "Email change details", required = true)
            ChangeEmailRequest request
    ) {
        return customerService.changeEmail(request);
    }

    @Operation(
        summary = "Confirm email change",
        description = "Confirm email change using token received in email"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Email change confirmed successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid or expired token"
        )
    })
    @GetMapping("/confirm-email-change/{token}")
    public String confirmEmailChange(
            @Parameter(description = "Email change confirmation token", required = true)
            @PathVariable String token
    ) {
        return customerService.confirmEmailChange(token);
    }
}
