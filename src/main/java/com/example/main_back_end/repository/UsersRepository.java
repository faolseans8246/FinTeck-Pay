package com.example.main_back_end.repository;

import com.example.main_back_end.entity.AuthUser;
import com.example.main_back_end.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {

    Optional<Users> findByAuthUser(AuthUser authUser);
    Optional<Users> findByAuthUser_Username(String username);
}
