package com.example.main_back_end.dto.request;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword
) {
}
