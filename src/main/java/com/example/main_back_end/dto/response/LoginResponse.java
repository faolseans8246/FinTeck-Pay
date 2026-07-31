package com.example.main_back_end.dto.response;

public record LoginResponse(
        String accessToken,
        String role,
        String username
) {
}
