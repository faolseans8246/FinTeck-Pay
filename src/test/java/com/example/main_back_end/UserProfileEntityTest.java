package com.example.main_back_end;

import com.example.main_back_end.entity.Users;
import com.example.main_back_end.model.Address;
import com.example.main_back_end.model.Passport;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserProfileEntityTest {

    @Test
    void shouldMapProfileAddressAndPassport() {
        Users user = Users.builder()
                .firstName("Ali")
                .lastName("Valiyev")
                .address(Address.builder()
                        .country("Uzbekistan")
                        .city("Tashkent")
                        .region("Toshkent")
                        .street("Amir Temur ko'chasi")
                        .home(24)
                        .build())
                .passport(Passport.builder()
                        .passportSeries("AA")
                        .passportNumber("1234567")
                        .issuedBy("Toshkent IIB")
                        .build())
                .build();

        assertThat(user.getAddress().getCity()).isEqualTo("Tashkent");
        assertThat(user.getPassport().getPassportNumber()).isEqualTo("1234567");
    }
}
