package com.example.main_back_end.repository;

import com.example.main_back_end.entity.CardBalance;
import com.example.main_back_end.roles.CurrencyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardBalanceRepository
        extends JpaRepository<CardBalance, UUID> {

    List<CardBalance> findAllByCardId(UUID cardId);

    Optional<CardBalance> findByCardIdAndCurrency(
            UUID cardId,
            CurrencyType currency
    );
}