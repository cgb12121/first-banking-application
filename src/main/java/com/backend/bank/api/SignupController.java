package com.backend.bank.api;
import com.backend.bank.dto.request.SignupRequest;
import com.backend.bank.dto.response.SignupResponse;
import com.backend.bank.exception.AccountAlreadyExistsException;
import com.backend.bank.service.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SignupController {

    private final SignupService signupService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest signupRequest) throws AccountAlreadyExistsException {
        SignupResponse response = signupService.signup(signupRequest);
        return ResponseEntity.ok(response);
    }
}
