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
    ApiResponse<LoginResponse> completeRegistration(com.example.main_back_end.dto.request.CompleteRegistrationRequest completeRegistrationRequest);
    ApiResponse<com.example.main_back_end.dto.response.ApiMessageResponse> requestPasswordReset(com.example.main_back_end.dto.request.RequestPasswordResetRequest requestPasswordResetRequest);
    ApiResponse<com.example.main_back_end.dto.response.VerifyResetResponse> verifyResetCode(com.example.main_back_end.dto.request.VerifyResetCodeRequest verifyResetCodeRequest);
    ApiResponse<com.example.main_back_end.dto.response.ApiMessageResponse> resetPassword(com.example.main_back_end.dto.request.ResetPasswordRequest resetPasswordRequest);
    ApiResponse<com.example.main_back_end.dto.response.ApiMessageResponse> changePassword(com.example.main_back_end.dto.request.ChangePasswordRequest changePasswordRequest, String currentUsername);
    ApiResponse<com.example.main_back_end.dto.response.ApiMessageResponse> logout(String refreshToken);
    ApiResponse<com.example.main_back_end.dto.response.LoginResponse> refresh(String token);
    ApiResponse<CreateAdminResponse> createAdmin(CreateAdminRequest createAdminRequest, String currentUsername);
}
