package com.example.main_back_end.notification;

public interface NotificationService {
    void sendOtpToEmail(String email, String otp);
    void sendOtpToPhone(String phone, String otp);
    void sendResetCodeToEmail(String email, String code);
    void sendResetCodeToPhone(String phone, String code);
}
