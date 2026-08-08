package com.example.main_back_end.security;

import com.example.main_back_end.entity.AuthUser;
import com.example.main_back_end.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthUserRepository authUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUser user = authUserRepository.findByUsername(username)
                .orElseGet(() -> authUserRepository.findByEmailOrPhone(username, username).orElse(null));

        if (user == null) {
            throw new UsernameNotFoundException("Foydalanuvchi topilmadi: " + username);
        }

        String roleName = user.getRole() != null ? user.getRole().name() : "USER";
        String authority = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;

        return User.withUsername(user.getUsername())
                .password(user.getPassword() == null ? "" : user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(authority)))
                .disabled(!user.isEnabled())
                .build();
    }
}
