package com.abhishek.expense.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.abhishek.expense.domain.Expense;
import com.abhishek.expense.domain.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class RepositoryPostgresIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.flyway.baseline-on-migrate", () -> false);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Test
    void persistsExistingPrismaCompatibleSchema() {
        User user = userRepository.save(User.create("Test User", "test@example.com", "hash"));
        expenseRepository.save(
            Expense.create(
                user,
                new BigDecimal("499.00"),
                "Groceries",
                "Food",
                LocalDateTime.of(2026, 9, 9, 0, 0)
            )
        );

        assertThat(expenseRepository.findAllByUser_IdOrderByExpenseDateDesc(user.getId()))
            .singleElement()
            .extracting(Expense::getAmount)
            .isEqualTo(new BigDecimal("499.00"));
    }
}
