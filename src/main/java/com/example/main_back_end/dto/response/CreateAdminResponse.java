package com.example.main_back_end.dto.response;

import java.util.UUID;

public record CreateAdminResponse(
        String message,
        UUID adminId,
        String username
) {
}
