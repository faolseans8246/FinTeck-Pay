package com.example.main_back_end.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class UnionCard {

    @NotBlank(message = "Karta raqami bo'sh bo'lishi mumkin emas")
    @Pattern(
            regexp = "\\d{16}",
            message = "Karta raqami 16 xonali bo'lishi kerak"
    )
    @Column(
            name = "card_number",
            nullable = false,
            unique = true,
            length = 16
    )
    private String cardNumber;

    @Min(
            value = 1,
            message = "Expiration month 1 dan 12 gacha bo'lishi kerak"
    )
    @Max(
            value = 12,
            message = "Expiration month 1 dan 12 gacha bo'lishi kerak"
    )
    @Column(
            name = "expiration_month",
            nullable = false
    )
    private int expirationMonth;

    @Min(
            value = 2026,
            message = "Expiration year noto'g'ri"
    )
    @Column(
            name = "expiration_year",
            nullable = false
    )
    private int expirationYear;
}