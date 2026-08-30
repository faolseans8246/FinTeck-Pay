package com.example.main_back_end.dto.request;

import com.example.main_back_end.roles.BaseType;
import com.example.main_back_end.roles.CardTypes;
import jakarta.validation.constraints.*;

public record CreateCardRequest(

        @NotBlank(message = "Card number bo'sh bo'lmasligi kerak")
        @Pattern(
                regexp = "\\d{16}",
                message = "Card number 16 xonali bo'lishi kerak"
        )
        String cardNumber,

        @Min(
                value = 1,
                message = "Expiration month 1 dan kichik bo'lmasligi kerak"
        )
        @Max(
                value = 12,
                message = "Expiration month 12 dan katta bo'lmasligi kerak"
        )
        int expirationMonth,

        @Min(
                value = 2026,
                message = "Expiration year noto'g'ri"
        )
        int expirationYear,

        @NotNull(message = "Card type tanlanishi kerak")
        CardTypes cardType,

        @NotNull(message = "Base type tanlanishi kerak")
        BaseType baseType,

        @Pattern(
                regexp = "\\d{3}",
                message = "CVV 3 xonali bo'lishi kerak"
        )
        String cvv,

        @NotBlank(message = "PIN bo'sh bo'lmasligi kerak")
        @Pattern(
                regexp = "\\d{4}",
                message = "PIN 4 xonali bo'lishi kerak"
        )
        String pin

) {
}