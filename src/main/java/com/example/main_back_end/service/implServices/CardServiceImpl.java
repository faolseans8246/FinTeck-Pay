package com.example.main_back_end.service.implServices;

import com.example.main_back_end.dto.request.CreateCardRequest;
import com.example.main_back_end.dto.response.CardResponse;
import com.example.main_back_end.entity.CardBalance;
import com.example.main_back_end.entity.Cards;
import com.example.main_back_end.entity.Users;
import com.example.main_back_end.model.CardSecurity;
import com.example.main_back_end.model.UnionCard;
import com.example.main_back_end.payload.ApiResponse;
import com.example.main_back_end.repository.CardBalanceRepository;
import com.example.main_back_end.repository.CardRepository;
import com.example.main_back_end.repository.UsersRepository;
import com.example.main_back_end.roles.CardTypes;
import com.example.main_back_end.roles.CardStatus;
import com.example.main_back_end.roles.CurrencyType;
import com.example.main_back_end.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CardBalanceRepository cardBalanceRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public ApiResponse<CardResponse> create(
            CreateCardRequest request,
            UUID userId
    ) {

        Users user = usersRepository
                .findById(userId)
                .orElse(null);

        if (user == null) {
            return ApiResponse.error(
                    "User topilmadi"
            );
        }


        if (cardRepository.existsByUnionCardCardNumber(
                request.cardNumber()
        )) {

            return ApiResponse.error(
                    "Bu karta raqami allaqachon mavjud"
            );
        }


        /*
         * VISA uchun CVV majburiy.
         */
        if (request.cardType() == CardTypes.VISA &&
                (request.cvv() == null ||
                        request.cvv().isBlank())) {

            return ApiResponse.error(
                    "VISA karta uchun CVV majburiy"
            );
        }


        /*
         * VISA bo'lmagan kartalarda
         * CVV kelgan bo'lsa ham qabul qilmaslik mumkin.
         */
        if (request.cardType() != CardTypes.VISA &&
                request.cvv() != null &&
                !request.cvv().isBlank()) {

            return ApiResponse.error(
                    "Faqat VISA karta uchun CVV kiritiladi"
            );
        }


        /*
         * Card yaratish.
         */
        Cards card = Cards.builder()

                .unionCard(
                        UnionCard.builder()
                                .cardNumber(
                                        request.cardNumber()
                                )
                                .expirationMonth(
                                        request.expirationMonth()
                                )
                                .expirationYear(
                                        request.expirationYear()
                                )
                                .build()
                )

                .cardSecurity(
                        CardSecurity.builder()
                                .pinHash(
                                        passwordEncoder.encode(
                                                request.pin()
                                        )
                                )
                                .cvv(
                                        request.cvv()
                                )
                                .build()
                )

                .cardTypes(
                        request.cardType()
                )

                .baseType(
                        request.baseType()
                )

                .user(user)

                .build();


        Cards savedCard =
                cardRepository.save(card);

        if (user.getCards() == null) {
            user.setCards(new java.util.ArrayList<>());
        }
        user.getCards().add(savedCard);
        usersRepository.save(user);

        /*
         * UZS, USD, EUR, RUB balanslarini
         * boshlang'ich 0 bilan yaratamiz.
         */
        createInitialBalances(savedCard);


        CardResponse response =
                mapToResponse(savedCard);


        return ApiResponse.success(
                "Card muvaffaqiyatli saqlandi",
                response
        );
    }


    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<CardResponse>> getMyCards(
            UUID userId
    ) {

        List<CardResponse> cards =
                cardRepository
                        .findAllByUserId(userId)
                        .stream()
                        .map(this::mapToResponse)
                        .toList();


        return ApiResponse.success(
                "Cardlar muvaffaqiyatli olindi",
                cards
        );
    }


    @Override
    @Transactional(readOnly = true)
    public ApiResponse<CardResponse> getMyCard(
            UUID userId,
            UUID cardId
    ) {

        Cards card =
                cardRepository
                        .findByIdAndUserId(
                                cardId,
                                userId
                        )
                        .orElse(null);


        if (card == null) {
            return ApiResponse.error(
                    "Card topilmadi"
            );
        }


        return ApiResponse.success(
                "Card muvaffaqiyatli olindi",
                mapToResponse(card)
        );
    }


    @Override
    public ApiResponse<Void> delete(
            UUID userId,
            UUID cardId
    ) {

        Cards card =
                cardRepository
                        .findByIdAndUserId(
                                cardId,
                                userId
                        )
                        .orElse(null);


        if (card == null) {
            return ApiResponse.error(
                    "Card topilmadi"
            );
        }


        cardRepository.delete(card);


        return ApiResponse.success(
                "Card muvaffaqiyatli o'chirildi",
                null
        );
    }

        @Override
        public ApiResponse<CardResponse> freeze(UUID userId, UUID cardId) {
                return changeStatus(cardRepository.findByIdAndUserId(cardId, userId).orElse(null), CardStatus.FROZEN);
        }

        @Override
        public ApiResponse<CardResponse> unfreeze(UUID userId, UUID cardId) {
                return changeStatus(cardRepository.findByIdAndUserId(cardId, userId).orElse(null), CardStatus.ACTIVE);
        }

        @Override
        public ApiResponse<CardResponse> block(UUID cardId) {
                return changeStatus(cardRepository.findById(cardId).orElse(null), CardStatus.BLOCKED);
        }

        @Override
        public ApiResponse<CardResponse> unblock(UUID cardId) {
                return changeStatus(cardRepository.findById(cardId).orElse(null), CardStatus.ACTIVE);
        }

        private ApiResponse<CardResponse> changeStatus(Cards card, CardStatus status) {
                if (card == null) {
                        return ApiResponse.error("Card topilmadi");
                }
                card.setCardStatus(status);
                return ApiResponse.success("Card holati yangilandi", mapToResponse(card));
        }


    // =====================================================
    // BALANCE
    // =====================================================

    private void createInitialBalances(
            Cards card
    ) {

        for (CurrencyType currency :
                CurrencyType.values()) {

            CardBalance balance =
                    CardBalance.builder()
                            .card(card)
                            .currency(currency)
                            .balance(BigDecimal.ZERO)
                            .build();

            cardBalanceRepository.save(balance);
        }
    }


    // =====================================================
    // MAPPER
    // =====================================================

    private CardResponse mapToResponse(
            Cards card
    ) {

        List<CardResponse.BalanceResponse> balances =
                cardBalanceRepository
                        .findAllByCardId(card.getId())
                        .stream()
                        .map(balance ->
                                new CardResponse.BalanceResponse(
                                        balance.getCurrency(),
                                        balance.getBalance()
                                )
                        )
                        .toList();


        return new CardResponse(

                card.getId(),

                maskCardNumber(
                        card.getUnionCard()
                                .getCardNumber()
                ),

                card.getUnionCard()
                        .getExpirationMonth(),

                card.getUnionCard()
                        .getExpirationYear(),

                card.getCardTypes(),

                card.getCardStatus(),

                card.getBaseType(),

                balances
        );
    }


    // =====================================================
    // CARD NUMBER MASK
    // =====================================================

    private String maskCardNumber(
            String cardNumber
    ) {

        if (cardNumber == null ||
                cardNumber.length() < 4) {

            return "****";
        }


        return "**** **** **** " +
                cardNumber.substring(
                        cardNumber.length() - 4
                );
    }
}