package com.example.main_back_end.controller;

import com.example.main_back_end.dto.request.CardPaymentRequest;
import com.example.main_back_end.dto.request.CardTransferRequest;
import com.example.main_back_end.dto.response.TransactionResponse;
import com.example.main_back_end.entity.AuthUser;
import com.example.main_back_end.entity.Users;
import com.example.main_back_end.payload.ApiResponse;
import com.example.main_back_end.service.TransferService;
import com.example.main_back_end.repository.AuthUserRepository;
import com.example.main_back_end.repository.UsersRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;
    private final AuthUserRepository authUsers;
    private final UsersRepository users;

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(@Valid @RequestBody CardTransferRequest request, Authentication authentication) {
        return ResponseEntity.ok(transferService.transfer(userId(authentication), request));
    }

    @PostMapping("/payment")
    public ResponseEntity<ApiResponse<TransactionResponse>> payment(@Valid @RequestBody CardPaymentRequest request, Authentication authentication) {
        return ResponseEntity.ok(transferService.payment(userId(authentication), request));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> history(Authentication authentication) {
        return ResponseEntity.ok(transferService.myHistory(userId(authentication)));
    }

    @GetMapping("/{transactionId}/check")
    public ResponseEntity<byte[]> check(@PathVariable UUID transactionId, Authentication authentication) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(transferService.getCheck(userId(authentication), transactionId));
    }

    private UUID userId(Authentication authentication) {
        AuthUser authUser = authUsers.findByUsername(authentication.getName()).orElseThrow(() -> new IllegalArgumentException("Auth user topilmadi"));
        Users user = users.findByAuthUser(authUser).orElseThrow(() -> new IllegalArgumentException("User topilmadi"));
        return user.getId();
    }
}