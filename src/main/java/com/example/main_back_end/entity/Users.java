package com.example.main_back_end.entity;

import com.example.main_back_end.index.Ids;
import com.example.main_back_end.model.Address;
import com.example.main_back_end.model.Passport;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Builder
@Table(name = "users_base")
public class Users extends Ids {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Embedded
    private Address address;

    @Embedded
    private Passport passport;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_user_id", referencedColumnName = "UUID", unique = true)
    private AuthUser authUser;

    @Past(message = "Tug'ilgan sana bugungi kundan oldin bo'lishi kerak")
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Cards> cards = new ArrayList<>();
}
