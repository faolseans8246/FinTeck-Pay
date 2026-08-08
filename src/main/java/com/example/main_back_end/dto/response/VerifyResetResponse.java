package com.example.main_back_end.dto.response;

public record VerifyResetResponse(
        String message,
        String resetToken
) {
}
