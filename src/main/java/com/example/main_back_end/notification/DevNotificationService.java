package com.example.main_back_end.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DevNotificationService implements NotificationService {

    @Override
    public void sendOtpToEmail(String email, String otp) {
        log.info("[DEV-EMAIL] To={} OTP={}", email, otp);
    }

    @Override
    public void sendOtpToPhone(String phone, String otp) {
        log.info("[DEV-SMS] To={} OTP={}", phone, otp);
    }

    @Override
    public void sendResetCodeToEmail(String email, String code) {
        log.info("[DEV-EMAIL] To={} RESET_CODE={}", email, code);
    }

    @Override
    public void sendResetCodeToPhone(String phone, String code) {
        log.info("[DEV-SMS] To={} RESET_CODE={}", phone, code);
    }
}
