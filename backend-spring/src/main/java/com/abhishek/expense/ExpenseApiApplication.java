package com.abhishek.expense;

import com.abhishek.expense.config.AiServiceProperties;
import com.abhishek.expense.config.AppCorsProperties;
import com.abhishek.expense.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, AppCorsProperties.class, AiServiceProperties.class})
public class ExpenseApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseApiApplication.class, args);
    }
}
