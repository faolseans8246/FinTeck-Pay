package com.example.main_back_end.controller;

import com.example.main_back_end.dto.request.LoginRequest;
import com.example.main_back_end.dto.request.RegisterRequest;
import com.example.main_back_end.dto.request.VerifyOtpRequest;
import com.example.main_back_end.dto.response.LoginResponse;
import com.example.main_back_end.dto.response.RegisterResponse;
import com.example.main_back_end.dto.response.VerifyOtpResponse;
import com.example.main_back_end.payload.ApiResponse;
import com.example.main_back_end.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        ApiResponse<RegisterResponse> apiResponse = authService.register(registerRequest);

        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<VerifyOtpResponse>> verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
        ApiResponse<VerifyOtpResponse> apiResponse = authService.verifyOtp(verifyOtpRequest);

        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PostMapping("/create-admin")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        ApiResponse<LoginResponse> apiResponse = authService.login(loginRequest);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }
}
