package com.example.main_back_end.dto.response;

public record VerifyOtpResponse(
        String message,
        String accessToken
) {
}
