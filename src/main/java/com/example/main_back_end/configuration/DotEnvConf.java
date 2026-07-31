package com.example.main_back_end.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotEnvConf {

    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure()
                .directory("./")           // .env fayl joylashgan joy
                .filename(".env")
                .ignoreIfMissing()    // .env topilmasa xato bersin
                .systemProperties()        // System properties ga ham qo‘shsin
                .load();
    }
}
