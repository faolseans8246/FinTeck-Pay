package com.example.main_back_end.dto.response;

import com.example.main_back_end.roles.CurrencyType;
import com.example.main_back_end.roles.TransactionStatus;
import com.example.main_back_end.roles.TransactionType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID sourceCardId,
        UUID targetCardId,
        BigDecimal amount,
        CurrencyType currency,
        TransactionType type,
        TransactionStatus status,
        String description,
        String checkPath,
        Timestamp createdAt
) {}