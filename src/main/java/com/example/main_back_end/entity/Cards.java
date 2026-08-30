package com.example.main_back_end.entity;

import com.example.main_back_end.index.Ids;
import com.example.main_back_end.model.CardSecurity;
import com.example.main_back_end.model.UnionCard;
import com.example.main_back_end.roles.BaseType;
import com.example.main_back_end.roles.CardStatus;
import com.example.main_back_end.roles.CardTypes;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "credit_cards")
public class Cards extends Ids {

    @OneToMany(
            mappedBy = "card",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<CardBalance> cardBalances = new ArrayList<>();

    @Embedded
    private UnionCard unionCard;

    @Embedded
    private CardSecurity cardSecurity;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "card_type",
            nullable = false
    )
    private CardTypes cardTypes;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "card_status",
            nullable = false
    )
    @Builder.Default
    private CardStatus cardStatus = CardStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "base_type",
            nullable = false
    )
    private BaseType baseType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private Users user;
}