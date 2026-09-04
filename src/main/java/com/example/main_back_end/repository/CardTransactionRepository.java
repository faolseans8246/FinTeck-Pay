package com.example.main_back_end.repository;

import com.example.main_back_end.entity.CardTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CardTransactionRepository extends JpaRepository<CardTransaction, UUID> {
    List<CardTransaction> findAllBySourceCardUserIdOrTargetCardUserIdOrderByCreateAtDesc(UUID sourceUserId, UUID targetUserId);
    List<CardTransaction> findAllByOrderByCreateAtDesc();
}