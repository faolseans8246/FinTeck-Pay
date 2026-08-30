package com.example.main_back_end.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class MainSecurity {

    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                // REST API bo'lgani uchun CSRF o'chiriladi
                .csrf(csrf -> csrf.disable())

                // CORS konfiguratsiyasi
                .cors(cors -> cors.configure(http))

                .authorizeHttpRequests(auth -> auth

                        // Authentication endpointlari ochiq
                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        // Swagger ochiq
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // User API faqat login qilgan userlar uchun
                        .requestMatchers(
                                "/api/users/**"
                        ).authenticated()

                        // Card API faqat login qilgan userlar uchun
                        .requestMatchers(
                                "/api/cards/**"
                        ).authenticated()

                        // Admin API faqat ADMIN uchun
                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        // Programmist API faqat PROGRAMMIST uchun
                        .requestMatchers(
                                "/api/programmist/**"
                        ).hasRole("PROGRAMMIST")

                        // Qolgan barcha endpointlar authentication talab qiladi
                        .anyRequest().authenticated()
                )

                // JWT ishlatilgani sababli session saqlanmaydi
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // JWT filter UsernamePasswordAuthenticationFilter'dan oldin ishlaydi
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}