package com.example.main_back_end.service.implServices;

import com.example.main_back_end.dto.request.CreateAdminRequest;
import com.example.main_back_end.dto.request.LoginRequest;
import com.example.main_back_end.dto.request.RegisterRequest;
import com.example.main_back_end.dto.request.VerifyOtpRequest;
import com.example.main_back_end.dto.response.CreateAdminResponse;
import com.example.main_back_end.dto.response.LoginResponse;
import com.example.main_back_end.dto.response.RegisterResponse;
import com.example.main_back_end.dto.response.VerifyOtpResponse;
import com.example.main_back_end.entity.AuthUser;
import com.example.main_back_end.payload.ApiResponse;
import com.example.main_back_end.repository.AuthUserRepository;
import com.example.main_back_end.roles.Roles;
import com.example.main_back_end.security.JwtUtil;
import com.example.main_back_end.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final com.example.main_back_end.notification.NotificationService notificationService;

    private static final String PROGRAMMIST_LOGIN = "Login";
    private static final String PROGRAMMIST_PASSWORD = "Parol";

    /**
     * Registration qismi bilan ishlash
     * @param registerRequest
     * @return
     */
    @Override
    @Transactional
    public ApiResponse<RegisterResponse> register(RegisterRequest registerRequest) {

        if ((registerRequest.email() == null || registerRequest.email().isBlank()) &&
                (registerRequest.phone() == null || registerRequest.phone().isBlank())) {
            return ApiResponse.error("Email yoki telefonraqam majburiy kiritilish kerak!");
        }

        String identifier = (registerRequest.email() != null && !registerRequest.email().isBlank())
            ? registerRequest.email() : registerRequest.phone();

        if (authUserRepository.existsByEmailOrPhone(registerRequest.email(), registerRequest.phone())) {
            return ApiResponse.error("Bu email yoki telefon bazada mavjud!");
        }

        String otp = generateOtp();

        AuthUser user = AuthUser.builder()
            .email(registerRequest.email())
            .phone(registerRequest.phone())
            .username(identifier)
            .role(Roles.USER)
            .enabled(false)
            .otpCode(otp)
            .otpExpiry(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5))
            .build();

        authUserRepository.save(user);

        // TODO: OTP yuborish
        if (registerRequest.email() != null && !registerRequest.email().isBlank()) {
            notificationService.sendOtpToEmail(registerRequest.email(), otp);
        } else if (registerRequest.phone() != null && !registerRequest.phone().isBlank()) {
            notificationService.sendOtpToPhone(registerRequest.phone(), otp);
        }

        RegisterResponse data = new RegisterResponse("OTP yuborildi", identifier);
        return ApiResponse.success("Ro'yhatdan o'tish muvaffaqiyatli boshlandi!", data);
    }

    /**
     * Verifikatsiya qismi bilan ishlash
     * @param verifyOtpRequest
     * @return
     */
    @Override
    @Transactional
    public ApiResponse<VerifyOtpResponse> verifyOtp(VerifyOtpRequest verifyOtpRequest) {

        AuthUser user = authUserRepository.findByEmailOrPhone(verifyOtpRequest.identifier(), verifyOtpRequest.identifier()).orElse(null);

        if (user == null) {
            return ApiResponse.error("Foydalanuvchi topilmadi!");
        }

        if (user.getOtpCode() == null || !user.getOtpCode().equals(verifyOtpRequest.otpCode())) {
            return ApiResponse.error("Noto'g'ri OTP code!");
        }

        if (user.getOtpExpiry() == null || user.getOtpExpiry() < System.currentTimeMillis()) {
            return ApiResponse.error("OTP time tugagan!");
        }

        user.setEnabled(true);
        user.setOtpCode(null);
        user.setOtpExpiry(null);

        String subject = user.getUsername() != null ? user.getUsername() : verifyOtpRequest.identifier();
        user.setUsername(subject);
        authUserRepository.save(user);

        String token = jwtUtil.generateToken(subject);
        VerifyOtpResponse data = new VerifyOtpResponse("OTP tasdiqlandi", token);

        return ApiResponse.success("Hisob tasdiqlandi. Iltimos, login va parol yarating.", data);
    }

    /**
     * Login qismi bilan ishlash
     * @param loginRequest
     * @return
     */
    @Override
    @Transactional
    public ApiResponse<LoginResponse> login(LoginRequest loginRequest) {

        // Programmmist kirish qismi
        if (PROGRAMMIST_LOGIN.equals(loginRequest.username()) &&
        PROGRAMMIST_PASSWORD.equals(loginRequest.password())) {

            String token = jwtUtil.generateToken(PROGRAMMIST_LOGIN);
            LoginResponse date = new LoginResponse(token, Roles.PROGRAMMIST.name(), PROGRAMMIST_LOGIN);

            return ApiResponse.success("Programmist muvaffaqiyatli kirdi!", date);
        }

        AuthUser user = authUserRepository.findByUsername(loginRequest.username()).orElse(null);

        if (user == null) {
            return ApiResponse.error("Login yoki parol noto'g'ri");
        }

        if (!user.isEnabled()) {
            return ApiResponse.error("Hisob hali tasdiqlanmagan, Avval OTP ni tasdiqlang!");
        }

        if (user.getPassword() == null || !passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            return ApiResponse.error("Login yoki Parol noto'g'ri");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        LoginResponse data = new LoginResponse(token, user.getRole().name(), user.getUsername());

        return ApiResponse.success("Muvaffaqiyatli kirildi", data);
    }

    @Override
    @Transactional
    public ApiResponse<LoginResponse> completeRegistration(com.example.main_back_end.dto.request.CompleteRegistrationRequest completeRegistrationRequest) {

        AuthUser user = authUserRepository.findByEmailOrPhone(completeRegistrationRequest.identifier(), completeRegistrationRequest.identifier()).orElse(null);

        if (user == null) {
            return ApiResponse.error("Foydalanuvchi topilmadi!");
        }

        if (!user.isEnabled()) {
            return ApiResponse.error("Ilk avval OTP ni tasdiqlang!");
        }

        if (authUserRepository.existsByUsername(completeRegistrationRequest.login())) {
            return ApiResponse.error("Bu login allaqachon foydalanilmoqda!");
        }

        user.setUsername(completeRegistrationRequest.login());
        user.setPassword(passwordEncoder.encode(completeRegistrationRequest.password()));

        AuthUser saved = authUserRepository.save(user);

        String token = jwtUtil.generateToken(saved.getUsername());
        LoginResponse data = new LoginResponse(token, saved.getRole().name(), saved.getUsername());

        return ApiResponse.success("Ro'yhatdan to'liq o'tildi", data);
    }

    @Override
    @Transactional
    public ApiResponse<com.example.main_back_end.dto.response.ApiMessageResponse> requestPasswordReset(com.example.main_back_end.dto.request.RequestPasswordResetRequest requestPasswordResetRequest) {

        if ((requestPasswordResetRequest.email() == null || requestPasswordResetRequest.email().isBlank()) &&
                (requestPasswordResetRequest.phone() == null || requestPasswordResetRequest.phone().isBlank())) {
            return ApiResponse.error("Email yoki telefon majburiy");
        }

        AuthUser user = authUserRepository.findByEmailOrPhone(requestPasswordResetRequest.email(), requestPasswordResetRequest.phone()).orElse(null);

        if (user == null) return ApiResponse.error("Foydalanuvchi topilmadi");

        String code = generateOtp();
        user.setResetCode(code);
        user.setResetExpiry(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15));
        authUserRepository.save(user);

        if (requestPasswordResetRequest.email() != null && !requestPasswordResetRequest.email().isBlank()) {
            notificationService.sendResetCodeToEmail(requestPasswordResetRequest.email(), code);
        } else {
            notificationService.sendResetCodeToPhone(requestPasswordResetRequest.phone(), code);
        }

        return ApiResponse.success(new com.example.main_back_end.dto.response.ApiMessageResponse("Reset kodi yuborildi"));
    }

    @Override
    @Transactional
    public ApiResponse<com.example.main_back_end.dto.response.VerifyResetResponse> verifyResetCode(com.example.main_back_end.dto.request.VerifyResetCodeRequest verifyResetCodeRequest) {

        AuthUser user = authUserRepository.findByEmailOrPhone(verifyResetCodeRequest.identifier(), verifyResetCodeRequest.identifier()).orElse(null);
        if (user == null) return ApiResponse.error("Foydalanuvchi topilmadi");

        if (user.getResetCode() == null || !user.getResetCode().equals(verifyResetCodeRequest.code())) {
            return ApiResponse.error("Noto'g'ri reset kodi");
        }

        if (user.getResetExpiry() == null || user.getResetExpiry() < System.currentTimeMillis()) {
            return ApiResponse.error("Reset kodi muddati tugagan");
        }

        String resetToken = java.util.UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetCode(null);
        user.setResetExpiry(null);
        authUserRepository.save(user);

        com.example.main_back_end.dto.response.VerifyResetResponse data = new com.example.main_back_end.dto.response.VerifyResetResponse("Reset token berildi", resetToken);
        return ApiResponse.success(data);
    }

    @Override
    @Transactional
    public ApiResponse<com.example.main_back_end.dto.response.ApiMessageResponse> resetPassword(com.example.main_back_end.dto.request.ResetPasswordRequest resetPasswordRequest) {

        if (resetPasswordRequest.resetToken() == null || resetPasswordRequest.resetToken().isBlank()) {
            return ApiResponse.error("resetToken kerak");
        }

        AuthUser user = authUserRepository.findAll().stream()
                .filter(u -> resetPasswordRequest.resetToken().equals(u.getResetToken()))
                .findFirst().orElse(null);

        if (user == null) return ApiResponse.error("Noto'g'ri reset token");

        user.setPassword(passwordEncoder.encode(resetPasswordRequest.newPassword()));
        user.setResetToken(null);
        authUserRepository.save(user);

        return ApiResponse.success(new com.example.main_back_end.dto.response.ApiMessageResponse("Parol muvaffaqiyatli o'zgartirildi"));
    }

    @Override
    @Transactional
    public ApiResponse<com.example.main_back_end.dto.response.ApiMessageResponse> changePassword(com.example.main_back_end.dto.request.ChangePasswordRequest changePasswordRequest, String currentUsername) {

        AuthUser user = authUserRepository.findByUsername(currentUsername).orElse(null);
        if (user == null) return ApiResponse.error("Foydalanuvchi topilmadi");

        if (!passwordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())) {
            return ApiResponse.error("Eski parol noto'g'ri");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.newPassword()));
        authUserRepository.save(user);

        return ApiResponse.success(new com.example.main_back_end.dto.response.ApiMessageResponse("Parol muvaffaqiyatli o'zgartirildi"));
    }

    @Override
    public ApiResponse<com.example.main_back_end.dto.response.ApiMessageResponse> logout(String refreshToken) {
        // Stateless JWT: nothing to do unless you implement token blacklist.
        return ApiResponse.success(new com.example.main_back_end.dto.response.ApiMessageResponse("Chiqish muvaffaqiyatli"));
    }

    @Override
    public ApiResponse<LoginResponse> refresh(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            if (jwtUtil.validateToken(token, username)) {
                String newToken = jwtUtil.generateToken(username);
                LoginResponse data = new LoginResponse(newToken, authUserRepository.findByUsername(username).map(u -> u.getRole().name()).orElse(Roles.USER.name()), username);
                return ApiResponse.success(data);
            } else {
                return ApiResponse.error("Token yaroqsiz");
            }
        } catch (Exception e) {
            return ApiResponse.error("Token parse xatosi");
        }
    }

    @Override
    @Transactional
    public ApiResponse<CreateAdminResponse> createAdmin(CreateAdminRequest createAdminRequest, String currentUsername) {

        if (!PROGRAMMIST_LOGIN.equals(currentUsername)) {
            return ApiResponse.error("Faqat programmist admin yarata oladi!");
        }

        if (authUserRepository.existsByUsername(createAdminRequest.username())) {
            return ApiResponse.error("BU username allaqachon mavjud!");
        }

        if (authUserRepository.existsByEmailOrPhone(createAdminRequest.email(), createAdminRequest.phone())) {
            return ApiResponse.error("Bu email yoki telefon raqam allaqachon mavjud!");
        }

        AuthUser admin = AuthUser.builder()
                .username(createAdminRequest.username())
                .password(passwordEncoder.encode(createAdminRequest.password()))
                .enabled(true)
                .build();

        AuthUser saved = authUserRepository.save(admin);

        CreateAdminResponse data = new CreateAdminResponse(
                "Admin muvaffaqiyatli yaratildi",
                saved.getId(),
                saved.getUsername()
        );

        return ApiResponse.success("Admin yaratildi!", data);
    }

    // ========== yordamchi metod ==========
    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}
