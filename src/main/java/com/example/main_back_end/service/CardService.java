package com.example.main_back_end.service;

import com.example.main_back_end.dto.request.CreateCardRequest;
import com.example.main_back_end.dto.response.CardResponse;
import com.example.main_back_end.payload.ApiResponse;

import java.util.List;
import java.util.UUID;

public interface CardService {

    ApiResponse<CardResponse> create(
            CreateCardRequest request,
            UUID userId
    );

    ApiResponse<List<CardResponse>> getMyCards(
            UUID userId
    );

    ApiResponse<CardResponse> getMyCard(
            UUID userId,
            UUID cardId
    );

    ApiResponse<Void> delete(
            UUID userId,
            UUID cardId
    );

        ApiResponse<CardResponse> freeze(UUID userId, UUID cardId);

        ApiResponse<CardResponse> unfreeze(UUID userId, UUID cardId);

        ApiResponse<CardResponse> block(UUID cardId);

        ApiResponse<CardResponse> unblock(UUID cardId);
}