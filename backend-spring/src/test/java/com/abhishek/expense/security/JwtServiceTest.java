package com.abhishek.expense.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.abhishek.expense.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

    @Test
    void createsAndVerifiesHs256Token() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test_secret_shared_with_node");
        properties.setExpiresIn("7d");
        JwtService jwtService = new JwtService(new ObjectMapper(), properties);
        User user = User.create("Abhishek", "abhi@example.com", "hash");
        ReflectionTestUtils.invokeMethod(user, "prePersist");

        String token = jwtService.createToken(user);
        JwtClaims claims = jwtService.verify(token);

        assertThat(claims.subject()).isEqualTo(user.getId());
        assertThat(claims.email()).isEqualTo("abhi@example.com");
    }

    @Test
    void rejectsTamperedToken() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test_secret_shared_with_node");
        JwtService jwtService = new JwtService(new ObjectMapper(), properties);
        User user = User.create("Abhishek", "abhi@example.com", "hash");
        ReflectionTestUtils.invokeMethod(user, "prePersist");
        String token = jwtService.createToken(user);
        char replacement = token.endsWith("A") ? 'B' : 'A';
        String tampered = token.substring(0, token.length() - 1) + replacement;

        assertThatThrownBy(() -> jwtService.verify(tampered))
            .isInstanceOf(InvalidTokenException.class);
    }
}
