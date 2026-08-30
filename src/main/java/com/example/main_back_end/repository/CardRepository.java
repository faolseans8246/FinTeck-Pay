package com.example.main_back_end.repository;

import com.example.main_back_end.entity.Cards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Cards, UUID> {

    List<Cards> findAllByUserId(UUID userId);

    Optional<Cards> findByIdAndUserId(
            UUID cardId,
            UUID userId
    );

    boolean existsByUnionCardCardNumber(
            String cardNumber
    );
}