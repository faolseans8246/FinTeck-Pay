package com.example.main_back_end.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConf implements WebMvcConfigurer {

    /**
     * CORS sozlamalari (Frontend bilan bog'lanish uchun juda muhim)
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                    // barcha endpointlar
                .allowedOriginPatterns("*")           // productionda aniq domen yozing (masalan: "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);              // cookie va Authorization header uchun
    }

    /**
     * Static resurslar (images, css, js va h.k.)
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");   // uploads papkasidagi fayllarni ochish

        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    // Agar kerak bo'lsa qo'shimcha metodlarni qo'shishingiz mumkin
}
