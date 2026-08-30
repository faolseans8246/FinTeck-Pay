package com.example.main_back_end.dto.request;

import com.example.main_back_end.roles.BaseType;
import com.example.main_back_end.roles.CardTypes;
import jakarta.validation.constraints.*;

public record CardRequest(

        @NotBlank
        @Pattern(regexp = "\\d{16}")
        String cardNumber,

        @Min(1)
        @Max(12)
        int expirationMonth,

        int expirationYear,

        @NotNull
        CardTypes cardTypes,

        @NotNull
        BaseType baseType,

        @Pattern(regexp = "\\d{3}")
        String cvv,

        @Pattern(regexp = "\\d{4}")
        String pin
) {
}
