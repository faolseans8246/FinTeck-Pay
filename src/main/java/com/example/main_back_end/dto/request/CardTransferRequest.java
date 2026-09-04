package com.example.main_back_end.dto.request;

import com.example.main_back_end.roles.CurrencyType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CardTransferRequest(
        @NotNull UUID sourceCardId,
        @NotNull UUID targetCardId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotNull CurrencyType currency,
        @Size(max = 500) String description
) {}