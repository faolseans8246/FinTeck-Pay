package com.example.main_back_end.controller;

import com.example.main_back_end.dto.request.CreateCardRequest;
import com.example.main_back_end.dto.response.CardResponse;
import com.example.main_back_end.entity.AuthUser;
import com.example.main_back_end.entity.Users;
import com.example.main_back_end.payload.ApiResponse;
import com.example.main_back_end.repository.AuthUserRepository;
import com.example.main_back_end.repository.UsersRepository;
import com.example.main_back_end.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(
        name = "Card Management",
        description = "Foydalanuvchining bank kartalarini boshqarish API'lari"
)
public class CardController {

    private final CardService cardService;
    private final UsersRepository usersRepository;
    private final AuthUserRepository authUserRepository;


    // =========================================================
    // CREATE CARD
    // =========================================================

    @Operation(
            summary = "Yangi karta yaratish",
            description = """
                    Joriy foydalanuvchi uchun yangi karta yaratadi.
                    
                    Foydalanuvchi JWT token orqali aniqlanadi.
                    User ID request body orqali yuborilmaydi.
                    
                    Karta yaratilganda UZS, USD, EUR va RUB
                    balanslari avtomatik ravishda yaratiladi.
                    """
    )
    @PostMapping
    public ResponseEntity<ApiResponse<CardResponse>> createCard(

            @Valid
            @RequestBody
            CreateCardRequest request,

            Authentication authentication
    ) {

        UUID userId = getUserId(authentication);

        ApiResponse<CardResponse> response =
                cardService.create(
                        request,
                        userId
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================================================
    // GET MY CARDS
    // =========================================================

    @Operation(
            summary = "Mening kartalarim",
            description = """
                    JWT token orqali joriy foydalanuvchini aniqlaydi
                    va foydalanuvchiga tegishli barcha kartalarni qaytaradi.
                    
                    Karta bilan birga UZS, USD, EUR va RUB
                    balanslari ham qaytariladi.
                    """
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<CardResponse>>> getMyCards(
            Authentication authentication
    ) {

        UUID userId = getUserId(authentication);

        ApiResponse<List<CardResponse>> response =
                cardService.getMyCards(userId);

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // GET ONE CARD
    // =========================================================

    @Operation(
            summary = "Bitta kartani olish",
            description = """
                    Berilgan cardId bo'yicha foydalanuvchining
                    kartasini qaytaradi.
                    
                    Foydalanuvchi faqat o'ziga tegishli
                    kartani ko'ra oladi.
                    """
    )
    @GetMapping("/{cardId}")
    public ResponseEntity<ApiResponse<CardResponse>> getMyCard(

            @Parameter(
                    description = "Kartaning UUID identifikatori",
                    required = true
            )
            @PathVariable UUID cardId,

            Authentication authentication
    ) {

        UUID userId = getUserId(authentication);

        ApiResponse<CardResponse> response =
                cardService.getMyCard(
                        userId,
                        cardId
                );

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // DELETE CARD
    // =========================================================

    @Operation(
            summary = "Kartani o'chirish",
            description = """
                    Foydalanuvchiga tegishli kartani o'chiradi.
                    
                    Faqat JWT token egasiga tegishli karta
                    o'chirilishi mumkin.
                    """
    )
    @DeleteMapping("/{cardId}")
    public ResponseEntity<ApiResponse<Void>> deleteCard(

            @Parameter(
                    description = "O'chiriladigan kartaning UUID identifikatori",
                    required = true
            )
            @PathVariable UUID cardId,

            Authentication authentication
    ) {

        UUID userId = getUserId(authentication);

        ApiResponse<Void> response =
                cardService.delete(
                        userId,
                        cardId
                );

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // GET USER ID
    // =========================================================

    /**
     * JWT orqali kelgan username/login yordamida
     * Users jadvalidan user UUID ni topadi.
     *
     * Sizning hozirgi JwtFilter'ingizda:
     *
     * authentication.getName()
     *
     * UUID emas, masalan:
     *
     * Login1
     *
     * qaytarmoqda.
     */
    private UUID getUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new IllegalArgumentException("Authentication yoki username topilmadi");
        }

        String username = authentication.getName();

        AuthUser authUser = authUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Auth user topilmadi: " + username));

        Users user = usersRepository.findByAuthUser(authUser)
                .orElseGet(() -> {
                    Users newUser = new Users();
                    newUser.setAuthUser(authUser);
                    return usersRepository.save(newUser);
                });

        return user.getId();
    }
}