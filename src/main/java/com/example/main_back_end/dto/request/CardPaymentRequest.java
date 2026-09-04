package com.example.main_back_end.dto.request;

import com.example.main_back_end.roles.CurrencyType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CardPaymentRequest(
        @NotNull UUID sourceCardId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotNull CurrencyType currency,
        @NotBlank @Size(max = 200) String merchant,
        @Size(max = 500) String description
) {}