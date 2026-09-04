package com.example.main_back_end.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {
    private final RestClient restClient = RestClient.create();

    @Value("${central-bank.rates-url:https://cbu.uz/uz/arkhiv-kursov-valyut/json/}")
    private String ratesUrl;

    @Value("${central-bank.api-key:}")
    private String apiKey;

    @GetMapping("/central-bank")
    public ResponseEntity<String> centralBankRates() {
        var request = restClient.get().uri(ratesUrl);
        if (apiKey != null && !apiKey.isBlank()) {
            request = request.header("X-API-Key", apiKey);
        }
        return ResponseEntity.ok(request.retrieve().body(String.class));
    }
}