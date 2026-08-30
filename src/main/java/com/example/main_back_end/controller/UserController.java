package com.example.main_back_end.controller;

import com.example.main_back_end.dto.request.CompleteProfileRequest;
import com.example.main_back_end.dto.response.ProfileResponse;
import com.example.main_back_end.payload.ApiResponse;
import com.example.main_back_end.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Foydalanuvchi shaxsiy ma'lumotlari va pasport ma'lumotlarini ko'rish va yangilash")
public class UserController {

    private final UserProfileService userProfileService;

    /**
     * Kirgan foydalanuvchining shaxsiy ma'lumotlarini ko'rish.
     */
    @Operation(summary = "Mening profilim", description = "Tizimga kirgan foydalanuvchining shaxsiy ma'lumotlari, yashash manzili va pasport ma'lumotlarini qaytaradi.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> getMyProfile(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        ApiResponse<ProfileResponse> response = userProfileService.getMyProfile(username);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    /**
     * Foydalanuvchi yashash manzili va pasport ma'lumotlarini to'ldirish yoki yangilash.
     */
    @Operation(summary = "Profilni to'ldirish yoki yangilash", description = "Foydalanuvchi yashash manzili, pasport ma'lumotlari, ism, familiya va tug'ilgan sanasini kiritishi yoki yangilashi mumkin.")
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> completeOrUpdateProfile(
            Authentication authentication,
            @Valid @RequestBody CompleteProfileRequest request) {
        String username = authentication != null ? authentication.getName() : null;
        ApiResponse<ProfileResponse> response = userProfileService.completeOrUpdateProfile(username, request);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }
}
