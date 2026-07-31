package com.example.main_back_end.entity;

import com.example.main_back_end.index.Ids;
import com.example.main_back_end.roles.Roles;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "Auth_base")
public class AuthUser extends Ids {

    private String username;            // email or phone number
    private String password;            // encoded

    private String email;

    private String phone;

    private Roles role;

    private boolean enabled = false;    // OTP tasdiqlangandan keyin true
    private String otpCode;
    private Long otpExpiry;             // millis
}
