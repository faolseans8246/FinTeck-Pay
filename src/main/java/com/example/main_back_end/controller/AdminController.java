package com.example.main_back_end.controller;

import com.example.main_back_end.dto.request.AdminDepositRequest;
import com.example.main_back_end.dto.response.CardResponse;
import com.example.main_back_end.dto.response.TransactionResponse;
import com.example.main_back_end.payload.ApiResponse;
import com.example.main_back_end.service.CardService;
import com.example.main_back_end.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final CardService cardService;
    private final TransferService transferService;

    @PostMapping("/deposits")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(@Valid @RequestBody AdminDepositRequest request) {
        return ResponseEntity.ok(transferService.deposit(request));
    }

    @PutMapping("/cards/{cardId}/block")
    public ResponseEntity<ApiResponse<CardResponse>> block(@PathVariable UUID cardId) {
        return ResponseEntity.ok(cardService.block(cardId));
    }

    @PutMapping("/cards/{cardId}/unblock")
    public ResponseEntity<ApiResponse<CardResponse>> unblock(@PathVariable UUID cardId) {
        return ResponseEntity.ok(cardService.unblock(cardId));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> transactions() {
        return ResponseEntity.ok(transferService.allHistory());
    }
}