package com.example.main_back_end.dto.request;

public record CreateAdminRequest(
        String username,
        String password,
        String email,
        String phone
) {
}
