package com.example.main_back_end.service;

import com.example.main_back_end.dto.request.CreateAdminRequest;
import com.example.main_back_end.dto.request.LoginRequest;
import com.example.main_back_end.dto.request.RegisterRequest;
import com.example.main_back_end.dto.request.VerifyOtpRequest;
import com.example.main_back_end.dto.response.CreateAdminResponse;
import com.example.main_back_end.dto.response.LoginResponse;
import com.example.main_back_end.dto.response.RegisterResponse;
import com.example.main_back_end.dto.response.VerifyOtpResponse;
import com.example.main_back_end.payload.ApiResponse;

public interface AuthService {

    ApiResponse<RegisterResponse> register(RegisterRequest registerRequest);
    ApiResponse<VerifyOtpResponse> verifyOtp(VerifyOtpRequest verifyOtpRequest);
    ApiResponse<LoginResponse> login(LoginRequest loginRequest);
    ApiResponse<CreateAdminResponse> createAdmin(CreateAdminRequest createAdminRequest, String currentUsername);
}
