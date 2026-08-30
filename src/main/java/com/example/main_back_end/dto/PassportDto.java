package com.example.main_back_end.dto;

public record PassportDto(
        String passportSeries,
        String passportNumber,
        String issuedBy,
        String issueDate
) {
}
