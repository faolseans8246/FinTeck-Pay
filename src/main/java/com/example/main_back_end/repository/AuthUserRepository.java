package com.example.main_back_end.repository;

import com.example.main_back_end.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {

    Optional<AuthUser> findByUsername(String username);
    boolean existsByUsername(String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM AuthUser u " +
            "WHERE (:email IS NOT NULL AND u.email = :email) OR (:phone IS NOT NULL AND u.phone = :phone)")
    boolean existsByEmailOrPhone(String email, String phone);

    @Query("SELECT u FROM AuthUser u WHERE u.email = :identifier OR u.phone = :identifier")
    Optional<AuthUser> findByEmailOrPhone(String identifier, String identifier2);
}
