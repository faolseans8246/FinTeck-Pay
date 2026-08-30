package com.example.main_back_end.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class CardSecurity {

    @Pattern(
            regexp = "\\d{3}",
            message = "CVV 3 xonali raqam bo'lishi kerak"
    )
    @Column(
            name = "card_cvv",
            length = 3
    )
    private String cvv;

    @Column(
            name = "card_pin_hash",
            nullable = false
    )
    private String pinHash;
}