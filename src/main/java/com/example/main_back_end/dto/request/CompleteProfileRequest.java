package com.example.main_back_end.dto.request;

import com.example.main_back_end.dto.AddressDto;
import com.example.main_back_end.dto.PassportDto;

import java.time.LocalDate;

public record CompleteProfileRequest(
        String firstName,
        String lastName,
        AddressDto addressDto,
        PassportDto passportDto,
        LocalDate birthDateDto
) {
}
