package com.example.main_back_end.dto.request;

public record VerifyResetCodeRequest(
        String identifier,
        String code
) {
}
