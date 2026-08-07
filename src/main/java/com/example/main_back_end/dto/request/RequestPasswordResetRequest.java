package com.example.main_back_end.dto.request;

public record RequestPasswordResetRequest(
        String email,
        String phone
) {
}
