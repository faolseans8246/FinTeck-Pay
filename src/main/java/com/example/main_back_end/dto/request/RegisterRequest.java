package com.example.main_back_end.dto.request;

public record RegisterRequest(
        String email,
        String phone,
        String password
) {
}
