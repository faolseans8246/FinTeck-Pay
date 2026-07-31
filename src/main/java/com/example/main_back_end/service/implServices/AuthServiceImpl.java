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
    private JwtUtil jwtUtil;
    // private final SmsService smsService;
    // private final EmailService emailService;

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
                .password(passwordEncoder.encode(registerRequest.password()))
                .role(Roles.USER)
                .enabled(false)
                .otpCode(otp)
                .otpExpiry(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5))
                .build();

        authUserRepository.save(user);

        // TODO: OTP yuborish
        // if (request.email() != null) emailService.sendOtp(request.email(), otp);
        // else smsService.sendOtp(request.phone(), otp);

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

        String token = jwtUtil.generateToken(user.getUsername());
        VerifyOtpResponse data = new VerifyOtpResponse("Token muvaffaqiyatli tasdiqlandi", token);

        return ApiResponse.success("DHisob tasdiqlandi", data);
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

        if (user.isEnabled()) {
            return ApiResponse.error("Hisob hali tasdiqlanmagan, Avval OTP ni tasdiqlang!");
        }

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            return ApiResponse.error("Login yoki Parol noto'g'ri");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        LoginResponse data = new LoginResponse(token, user.getRole().name(), user.getUsername());

        return ApiResponse.success("Muvaffaqiyatli kirildi", data);
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
                .email(createAdminRequest.email())
                .phone(createAdminRequest.phone())
                .role(Roles.ADMIN)
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
