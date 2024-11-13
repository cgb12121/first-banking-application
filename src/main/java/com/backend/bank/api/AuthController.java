package com.backend.bank.api;

import com.backend.bank.dto.request.LoginRequest;
import com.backend.bank.dto.request.SignupRequest;
import com.backend.bank.dto.response.LoginResponse;
import com.backend.bank.dto.response.SignupResponse;
import com.backend.bank.service.intf.LoginService;
import com.backend.bank.service.intf.SignupService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Log4j2
@CrossOrigin
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication management APIs including login, signup, and verification")
public class AuthController {

    LoginService loginService;

    SignupService signupService;

    @Operation(
        summary = "Register new user",
        description = "Create a new user account with email verification"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = SignupResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input provided",
            content = @Content(schema = @Schema(implementation = SignupResponse.class))
        )
    })
    @PostMapping("/signup")
    public CompletableFuture<ResponseEntity<SignupResponse>> signup(
            @RequestBody @Valid 
            @Parameter(description = "Signup details", required = true)
            SignupRequest signupRequest,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            SignupResponse errorResponse = new SignupResponse(errors);
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(errorResponse));
        }

        return this.signupService.signup(signupRequest)
                .thenApply(ResponseEntity::ok);
    }

    @Operation(
        summary = "Verify user account",
        description = "Verify user email using verification code"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid verification code")
    })
    @PostMapping("/verify/{verificationCode}")
    public CompletableFuture<ResponseEntity<String>> verifyAccount(
            @Parameter(description = "Email verification code", required = true)
            @PathVariable String verificationCode
    ) {
        return this.signupService.verifyUser(verificationCode)
                .thenApply(ResponseEntity::ok);
    }

    @Operation(
        summary = "Resend verification email",
        description = "Resend verification email to user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verification email sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/resend-verify-email")
    public void resendVerifyEmail(
            @RequestBody 
            @Parameter(description = "User signup details", required = true)
            SignupRequest signupRequest
    ) {
        this.signupService.resendVerificationEmail(signupRequest);
    }

    @Operation(
        summary = "User login",
        description = "Authenticate user and create session"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
        ),
        @ApiResponse(
            responseCode = "408",
            description = "Request timeout"
        )
    })
    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<LoginResponse>> login(
            @RequestBody @Valid 
            @Parameter(description = "Login credentials", required = true)
            LoginRequest loginRequest,
            BindingResult bindingResult,
            HttpServletResponse response,
            HttpSession session
    ) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            errors.addFirst(Instant.now().toString());

            LoginResponse errorResponse = new LoginResponse(errors);
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(errorResponse));
        }

        return loginService.login(loginRequest)
                .thenApply(loginResponse -> {
                    session.setAttribute("userId", loginResponse.getUserId());
                    session.setAttribute("username", loginResponse.getUsername());
                    session.setMaxInactiveInterval(30 * 60);

                    ResponseCookie cookie = ResponseCookie.from("authToken", loginResponse.getToken())
                            .httpOnly(true)
                            .secure(true)
                            .path("/")
                            .maxAge(24 * 60 * 60)
                            .sameSite("Strict")
                            .build();
                    
                    response.addHeader("Set-Cookie", cookie.toString());
                    
                    return ResponseEntity.ok()
                            .header("Set-Cookie", cookie.toString())
                            .body(loginResponse);
                })
                .orTimeout(1, TimeUnit.SECONDS);
    }

    @Operation(
        summary = "User logout",
        description = "Logout user and invalidate session"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Logout successful"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No authenticated user found"
        )
    })
    @PostMapping("/logout")
    public CompletableFuture<ResponseEntity<String>> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session
    ) {
        return CompletableFuture.supplyAsync(() -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                new SecurityContextLogoutHandler().logout(request, response, authentication);
                SecurityContextHolder.clearContext();
                
                session.invalidate();
                
                ResponseCookie cookie = ResponseCookie.from("authToken", "")
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .maxAge(0)
                        .sameSite("Strict")
                        .build();
                        
                return ResponseEntity.ok()
                        .header("Set-Cookie", cookie.toString())
                        .body("Logout successful.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authenticated user found.");
            }
        });
    }

}
