package com.example.main_back_end.dto.response;

import com.example.main_back_end.dto.AddressDto;

import java.time.LocalDate;
import java.util.UUID;

public record ProfileResponse(
        UUID id,
        String firstName,
        String lastName,
        AddressDto addressDto,
        LocalDate birthDateDto,
        String role
) {
}
