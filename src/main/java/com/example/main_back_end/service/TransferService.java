package com.example.main_back_end.service;

import com.example.main_back_end.dto.request.AdminDepositRequest;
import com.example.main_back_end.dto.request.CardPaymentRequest;
import com.example.main_back_end.dto.request.CardTransferRequest;
import com.example.main_back_end.dto.response.TransactionResponse;
import com.example.main_back_end.payload.ApiResponse;

import java.util.List;
import java.util.UUID;

public interface TransferService {
    ApiResponse<TransactionResponse> transfer(UUID userId, CardTransferRequest request);
    ApiResponse<TransactionResponse> payment(UUID userId, CardPaymentRequest request);
    ApiResponse<TransactionResponse> deposit(AdminDepositRequest request);
    ApiResponse<List<TransactionResponse>> myHistory(UUID userId);
    ApiResponse<List<TransactionResponse>> allHistory();
    byte[] getCheck(UUID userId, UUID transactionId);
}