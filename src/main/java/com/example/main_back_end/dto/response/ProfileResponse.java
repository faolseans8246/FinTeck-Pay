package com.example.main_back_end.dto.response;

import com.example.main_back_end.dto.AddressDto;
import com.example.main_back_end.dto.PassportDto;

import java.time.LocalDate;
import java.util.UUID;

public record ProfileResponse(
        UUID id,
        String username,
        String firstName,
        String lastName,
        AddressDto addressDto,
        PassportDto passportDto,
        LocalDate birthDateDto,
        String role
) {
}
