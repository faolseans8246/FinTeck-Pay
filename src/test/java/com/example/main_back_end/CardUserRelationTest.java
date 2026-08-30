package com.example.main_back_end;

import com.example.main_back_end.entity.Cards;
import com.example.main_back_end.entity.Users;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardUserRelationTest {

    @Test
    void userBuilderShouldCreateEmptyCardsList() {
        Users user = Users.builder()
                .firstName("Ali")
                .lastName("Valiyev")
                .build();

        assertNotNull(user.getCards());
        assertTrue(user.getCards().isEmpty());
    }

    @Test
    void cardShouldBelongToUserAndBeVisibleFromUser() {
        Users user = Users.builder()
                .firstName("Ali")
                .lastName("Valiyev")
                .build();

        Cards card = Cards.builder()
                .user(user)
                .build();

        user.getCards().add(card);

        assertEquals(user, card.getUser());
        assertTrue(user.getCards().contains(card));
    }
}
