package com.example.main_back_end.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConf {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clone Bank")
                        .description("FinTech loyihasi bilan ishlash va uni shakllantirish")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Programmist")
                                .email("mysimplebox123@gmail.com")
                                .url("https://clonebank.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")))

                // JWT uchun Security scheme (agar JWT ishlatayotgan bo'lsangiz)
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))

                .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
    }
}
