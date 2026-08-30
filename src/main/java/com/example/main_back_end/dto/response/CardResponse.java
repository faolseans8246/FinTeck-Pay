package com.example.main_back_end.dto.response;

import com.example.main_back_end.roles.BaseType;
import com.example.main_back_end.roles.CardStatus;
import com.example.main_back_end.roles.CardTypes;
import com.example.main_back_end.roles.CurrencyType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CardResponse(

        UUID id,

        String cardNumber,

        int expirationMonth,

        int expirationYear,

        CardTypes cardType,

        CardStatus cardStatus,

        BaseType baseType,

        List<BalanceResponse> balances

) {

    public record BalanceResponse(

            CurrencyType currency,

            BigDecimal amount

    ) {
    }
}