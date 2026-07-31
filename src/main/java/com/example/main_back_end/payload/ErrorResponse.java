package com.example.main_back_end.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private int status;
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> errors;

    // 4 parametrli konstruktor (BaseException lar uchun)
    public ErrorResponse(int status, String errorCode, String message, LocalDateTime timestamp) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = timestamp;
    }
}
