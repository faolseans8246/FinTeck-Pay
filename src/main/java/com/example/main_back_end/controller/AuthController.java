package com.example.main_back_end.controller;

import com.example.main_back_end.dto.request.LoginRequest;
import com.example.main_back_end.dto.request.CompleteRegistrationRequest;
import com.example.main_back_end.dto.request.RegisterRequest;
import com.example.main_back_end.dto.request.VerifyOtpRequest;
import com.example.main_back_end.dto.response.LoginResponse;
import com.example.main_back_end.dto.response.RegisterResponse;
import com.example.main_back_end.dto.response.VerifyOtpResponse;
import com.example.main_back_end.payload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.main_back_end.security.JwtUtil;
import com.example.main_back_end.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autentifikatsiya va foydalanuvchi ro'yxatga olish endpointlari (O'zbekcha izohlar)")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    /**
     * Foydalanuvchi ro'yxatdan o'tish bosqichi.
     * Email yoki telefon orqali OTP yuboriladi va hisobni tasdiqlash uchun keyingi qadamga o'tkaziladi.
     */
    @Operation(summary = "Ro'yxatga olish — OTP yuborish", description = "Email yoki telefon raqamni yuboring. Tizim sizga OTP kodini yuboradi (odatda 5 daqiqa ichida amal qiladi).")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        ApiResponse<RegisterResponse> apiResponse = authService.register(registerRequest);

        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    /**
     * OTP kodini tasdiqlash va foydalanuvchi hisobini faol holatga keltirish.
     */
    @Operation(summary = "OTP tasdiqlash", description = "Identifier (email yoki telefon) va OTP kodini yuboring. Agar kod to'g'ri bo'lsa, hisob tasdiqlanadi va keyingi `complete-registration` bosqichiga o'tishingiz mumkin.")
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<VerifyOtpResponse>> verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
        ApiResponse<VerifyOtpResponse> apiResponse = authService.verifyOtp(verifyOtpRequest);

        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    /**
     * Ro'yxatdan o'tishni yakunlash: login va parol tayinlash; keyin JWT token qaytariladi.
     */
    @Operation(summary = "Ro'yxatni yakunlash", description = "OTP tasdiqlangan identifier uchun `login` va `password` o'rnating. Muvaffaqiyatli yakunlanganda JWT token qaytariladi.")
    @PostMapping("/complete-registration")
    public ResponseEntity<ApiResponse<LoginResponse>> completeRegistration(@Valid @RequestBody CompleteRegistrationRequest completeRegistrationRequest) {
        ApiResponse<LoginResponse> apiResponse = authService.completeRegistration(completeRegistrationRequest);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    /**
     * Login va parol bilan tizimga kirish; JWT access token qaytariladi.
     */
    @Operation(summary = "Kirish (login/parol)", description = "O'rnatilgan `username` va `password` bilan tizimga kiring. Muvaffaqiyatli kirishda access token qaytariladi.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        ApiResponse<LoginResponse> apiResponse = authService.login(loginRequest);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @Operation(summary = "Admin yaratish (faqat programmist)", description = "Faqat programmist rolidagi foydalanuvchi admin yaratishi mumkin. So'rov Authorization header ichida programmist token bo'lishi kerak.")
    @PostMapping("/create-admin")
    public ResponseEntity<ApiResponse<com.example.main_back_end.dto.response.CreateAdminResponse>> createAdmin(
            @Valid @RequestBody com.example.main_back_end.dto.request.CreateAdminRequest createAdminRequest,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        String currentUsername = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                currentUsername = jwtUtil.extractUsername(authorizationHeader.substring(7));
            } catch (Exception ignored) {
            }
        }

        ApiResponse<com.example.main_back_end.dto.response.CreateAdminResponse> apiResponse = authService.createAdmin(createAdminRequest, currentUsername);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @Operation(summary = "Parol tiklash so'rovi", description = "Email yoki telefon raqam yuboring; tizim sizga parolni tiklash uchun kod yuboradi.")
    @PostMapping("/request-password-reset")
    public ResponseEntity<ApiResponse<com.example.main_back_end.dto.response.ApiMessageResponse>> requestPasswordReset(@Valid @RequestBody com.example.main_back_end.dto.request.RequestPasswordResetRequest request) {
        ApiResponse<com.example.main_back_end.dto.response.ApiMessageResponse> apiResponse = authService.requestPasswordReset(request);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @Operation(summary = "Reset kodni tekshirish", description = "Identifier va yuborilgan reset kodini tekshirish. Agar kod to'g'ri bo'lsa, `resetToken` qaytariladi.")
    @PostMapping("/verify-reset-code")
    public ResponseEntity<ApiResponse<com.example.main_back_end.dto.response.VerifyResetResponse>> verifyResetCode(@Valid @RequestBody com.example.main_back_end.dto.request.VerifyResetCodeRequest request) {
        ApiResponse<com.example.main_back_end.dto.response.VerifyResetResponse> apiResponse = authService.verifyResetCode(request);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @Operation(summary = "Parolni tiklash", description = "`resetToken` va yangi parol yuboring. Agar token to'g'ri bo'lsa, parol yangilanadi.")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<com.example.main_back_end.dto.response.ApiMessageResponse>> resetPassword(@Valid @RequestBody com.example.main_back_end.dto.request.ResetPasswordRequest request) {
        ApiResponse<com.example.main_back_end.dto.response.ApiMessageResponse> apiResponse = authService.resetPassword(request);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @Operation(summary = "Parolni o'zgartirish (auth)", description = "Avtorizatsiyalangan foydalanuvchi eski va yangi parolni yuborib parolni yangilaydi.")
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<com.example.main_back_end.dto.response.ApiMessageResponse>> changePassword(@Valid @RequestBody com.example.main_back_end.dto.request.ChangePasswordRequest request, org.springframework.security.core.Authentication authentication) {
        String currentUsername = authentication == null ? null : authentication.getName();
        ApiResponse<com.example.main_back_end.dto.response.ApiMessageResponse> apiResponse = authService.changePassword(request, currentUsername);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @Operation(summary = "Chiqish", description = "Foydalanuvchi hisobidan chiqish. Agar refresh-token blacklisting ishlatilsa, uni yuboring.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<com.example.main_back_end.dto.response.ApiMessageResponse>> logout(@RequestBody(required = false) java.util.Map<String, String> payload) {
        String refresh = payload == null ? null : payload.get("refreshToken");
        ApiResponse<com.example.main_back_end.dto.response.ApiMessageResponse> apiResponse = authService.logout(refresh);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @Operation(summary = "Token yangilash", description = "Mavjud token yordamida yangi access token olish uchun ishlatiladi.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<com.example.main_back_end.dto.response.LoginResponse>> refresh(@RequestBody java.util.Map<String, String> payload) {
        String token = payload.get("token");
        ApiResponse<com.example.main_back_end.dto.response.LoginResponse> apiResponse = authService.refresh(token);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }
}
