package com.example.main_back_end.entity;

import com.example.main_back_end.index.Ids;
import com.example.main_back_end.roles.CurrencyType;
import com.example.main_back_end.roles.TransactionStatus;
import com.example.main_back_end.roles.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "card_transactions")
@EqualsAndHashCode(callSuper = true)
public class CardTransaction extends Ids {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_card_id", nullable = false)
    private Cards sourceCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_card_id")
    private Cards targetCard;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(length = 500)
    private String description;

    @Column(name = "check_path")
    private String checkPath;
}