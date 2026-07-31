package com.example.main_back_end.dto.request;

public record LoginRequest(
        String username,
        String password
) {
}
