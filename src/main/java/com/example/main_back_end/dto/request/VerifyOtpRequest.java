package com.example.main_back_end.dto.request;

public record VerifyOtpRequest(
        String identifier,
        String otpCode
) {
}
