package com.example.main_back_end.dto.request;

public record CompleteRegistrationRequest(
        String identifier,
        String login,
        String password
) {
}
