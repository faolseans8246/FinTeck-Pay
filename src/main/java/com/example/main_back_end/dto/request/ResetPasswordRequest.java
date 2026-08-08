package com.example.main_back_end.dto.request;

public record ResetPasswordRequest(
        String resetToken,
        String newPassword
) {
}
