package com.abhishek.expenseai;

import com.abhishek.expenseai.config.GeminiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(GeminiProperties.class)
public class AiInsightsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiInsightsApplication.class, args);
    }
}
