package com.backend.bank.api;

import com.backend.bank.dto.request.ChangePhoneNumberRequest;
import com.backend.bank.dto.response.ChangePhoneNumberResponse;
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
@RequestMapping("/api/phone")
@Tag(name = "Phone Management", description = "APIs for managing customer phone numbers")
public class PhoneController {

    CustomerService customerService;

    @Operation(
        summary = "Change phone number",
        description = "Request to change customer's phone number. Sends confirmation via email and OTP"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Phone number change request processed successfully",
            content = @Content(schema = @Schema(implementation = ChangePhoneNumberResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid phone number or request format"
        )
    })
    @PostMapping("/change")
    public ChangePhoneNumberResponse changePhoneNumber(
            @RequestBody @Valid 
            @Parameter(description = "Phone number change details", required = true)
            ChangePhoneNumberRequest request
    ) {
        return customerService.changePhoneNumber(request);
    }

    @Operation(
        summary = "Confirm phone number change via email link",
        description = "Confirm phone number change using token received in email"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Phone number change confirmed successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid or expired token"
        )
    })
    @GetMapping("/confirm-phone-change/{token}")
    public String confirmPhoneNumberChange(
            @Parameter(description = "Confirmation token received in email", required = true)
            @PathVariable String token
    ) {
        return customerService.confirmPhoneNumberChangeByLinkOnEmail(token);
    }

    @Operation(
        summary = "Confirm phone number change via OTP",
        description = "Confirm phone number change using OTP sent to the new phone number"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Phone number change confirmed successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid OTP or phone number"
        )
    })
    @PostMapping("/confirm-phone-change-otp")
    public String confirmPhoneNumberChangeByOTP(
            @Parameter(description = "OTP received on new phone number", required = true)
            @RequestParam String otp,
            @Parameter(description = "New phone number to be confirmed", required = true)
            @RequestParam String newPhoneNumber
    ) {
        return customerService.confirmPhoneNumberChangeByOTP(otp, newPhoneNumber);
    }
}
