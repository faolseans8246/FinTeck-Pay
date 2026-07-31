package com.example.main_back_end.dto;

public record AddressDto(
        String country,
        String city,
        String region,
        String district,
        String street,
        int houseNumber
) {
}
