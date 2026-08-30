package com.example.main_back_end.entity;

import com.example.main_back_end.index.Ids;
import com.example.main_back_end.roles.CurrencyType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "card_balances",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_card_currency",
                        columnNames = {"card_id", "currency"}
                )
        }
)
public class CardBalance extends Ids {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "card_id",
            nullable = false
    )
    private Cards card;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "currency",
            nullable = false
    )
    private CurrencyType currency;

    @Column(
            name = "balance",
            nullable = false,
            precision = 19,
            scale = 2
    )
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;
}