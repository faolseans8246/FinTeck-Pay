package com.example.main_back_end.service.implServices;

import com.example.main_back_end.dto.request.*;
import com.example.main_back_end.dto.response.TransactionResponse;
import com.example.main_back_end.entity.CardBalance;
import com.example.main_back_end.entity.CardTransaction;
import com.example.main_back_end.entity.Cards;
import com.example.main_back_end.payload.ApiResponse;
import com.example.main_back_end.repository.CardBalanceRepository;
import com.example.main_back_end.repository.CardRepository;
import com.example.main_back_end.repository.CardTransactionRepository;
import com.example.main_back_end.roles.CardStatus;
import com.example.main_back_end.roles.TransactionStatus;
import com.example.main_back_end.roles.TransactionType;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TransferServiceImpl implements com.example.main_back_end.service.TransferService {
    private final CardRepository cardRepository;
    private final CardBalanceRepository balanceRepository;
    private final CardTransactionRepository transactionRepository;
    @Value("${storage.checks-directory:storage/checks}") private String checksDirectory;

    @Override
    public ApiResponse<TransactionResponse> transfer(UUID userId, CardTransferRequest request) {
        Cards source = cardRepository.findByIdAndUserId(request.sourceCardId(), userId).orElse(null);
        Cards target = cardRepository.findById(request.targetCardId()).orElse(null);
        if (source == null || target == null || source.getId().equals(target.getId())) return ApiResponse.error("Source yoki target card noto'g'ri");
        if (!usable(source) || !usable(target)) return ApiResponse.error("Card faol emas");
        CardBalance from = balance(source, request.currency());
        CardBalance to = balance(target, request.currency());
        if (from.getBalance().compareTo(request.amount()) < 0) return ApiResponse.error("Mablag' yetarli emas");
        from.setBalance(from.getBalance().subtract(request.amount()));
        to.setBalance(to.getBalance().add(request.amount()));
        return record(source, target, request.amount(), request.currency(), TransactionType.TRANSFER, request.description());
    }

    @Override
    public ApiResponse<TransactionResponse> payment(UUID userId, CardPaymentRequest request) {
        Cards source = cardRepository.findByIdAndUserId(request.sourceCardId(), userId).orElse(null);
        if (source == null || !usable(source)) return ApiResponse.error("Card faol emas yoki topilmadi");
        CardBalance from = balance(source, request.currency());
        if (from.getBalance().compareTo(request.amount()) < 0) return ApiResponse.error("Mablag' yetarli emas");
        from.setBalance(from.getBalance().subtract(request.amount()));
        return record(source, null, request.amount(), request.currency(), TransactionType.PAYMENT, request.merchant() + ": " + request.description());
    }

    @Override
    public ApiResponse<TransactionResponse> deposit(AdminDepositRequest request) {
        Cards card = cardRepository.findById(request.cardId()).orElse(null);
        if (card == null || card.getCardStatus() == CardStatus.BLOCKED) return ApiResponse.error("Card topilmadi yoki blocklangan");
        CardBalance balance = balance(card, request.currency());
        balance.setBalance(balance.getBalance().add(request.amount()));
        return record(card, null, request.amount(), request.currency(), TransactionType.DEPOSIT, request.description());
    }

    @Override @Transactional(readOnly = true)
    public ApiResponse<List<TransactionResponse>> myHistory(UUID userId) {
        return ApiResponse.success("Transaction tarixi", transactionRepository.findAllBySourceCardUserIdOrTargetCardUserIdOrderByCreateAtDesc(userId, userId).stream().map(this::map).toList());
    }

    @Override @Transactional(readOnly = true)
    public ApiResponse<List<TransactionResponse>> allHistory() {
        return ApiResponse.success("Barcha transactionlar", transactionRepository.findAllByOrderByCreateAtDesc().stream().map(this::map).toList());
    }

    @Override @Transactional(readOnly = true)
    public byte[] getCheck(UUID userId, UUID transactionId) {
        CardTransaction tx = transactionRepository.findById(transactionId).orElseThrow(() -> new IllegalArgumentException("Check topilmadi"));
        boolean owns = tx.getSourceCard().getUser().getId().equals(userId) || (tx.getTargetCard() != null && tx.getTargetCard().getUser().getId().equals(userId));
        if (!owns) throw new IllegalArgumentException("Check sizga tegishli emas");
        try { return Files.readAllBytes(Path.of(tx.getCheckPath())); } catch (IOException e) { throw new IllegalStateException("Check o'qilmadi", e); }
    }

    private ApiResponse<TransactionResponse> record(Cards source, Cards target, BigDecimal amount, com.example.main_back_end.roles.CurrencyType currency, TransactionType type, String description) {
        CardTransaction tx = transactionRepository.save(CardTransaction.builder().sourceCard(source).targetCard(target).amount(amount).currency(currency).type(type).status(TransactionStatus.SUCCESS).description(description).build());
        try {
            Path userDir = Path.of(checksDirectory, source.getUser().getId().toString());
            Files.createDirectories(userDir);
            Path check = userDir.resolve(tx.getId() + ".pdf");
            Files.write(check, pdf(tx), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            tx.setCheckPath(check.toString());
            if (target != null && !target.getUser().getId().equals(source.getUser().getId())) {
                Path targetDir = Path.of(checksDirectory, target.getUser().getId().toString());
                Files.createDirectories(targetDir);
                Files.copy(check, targetDir.resolve(check.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) { throw new IllegalStateException("Check saqlanmadi", e); }
        return ApiResponse.success("Transaction muvaffaqiyatli", map(tx));
    }

    private byte[] pdf(CardTransaction tx) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Document document = new Document(); PdfWriter.getInstance(document, output); document.open();
            document.add(new Paragraph("FinTeck-Pay TRANSACTION CHECK"));
            document.add(new Paragraph("ID: " + tx.getId()));
            document.add(new Paragraph("Type: " + tx.getType()));
            document.add(new Paragraph("Amount: " + tx.getAmount() + " " + tx.getCurrency()));
            document.add(new Paragraph("Description: " + tx.getDescription())); document.close(); return output.toByteArray();
        } catch (Exception e) { throw new IOException(e); }
    }

    private CardBalance balance(Cards card, com.example.main_back_end.roles.CurrencyType currency) { return balanceRepository.findByCardIdAndCurrency(card.getId(), currency).orElseThrow(() -> new IllegalArgumentException("Valyuta balansi topilmadi")); }
    private boolean usable(Cards card) { return card.getCardStatus() == CardStatus.ACTIVE; }
    private TransactionResponse map(CardTransaction tx) { return new TransactionResponse(tx.getId(), tx.getSourceCard().getId(), tx.getTargetCard() == null ? null : tx.getTargetCard().getId(), tx.getAmount(), tx.getCurrency(), tx.getType(), tx.getStatus(), tx.getDescription(), tx.getCheckPath(), tx.getCreateAt()); }
}