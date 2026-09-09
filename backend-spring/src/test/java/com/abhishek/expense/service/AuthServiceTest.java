package com.abhishek.expense.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.abhishek.expense.domain.User;
import com.abhishek.expense.dto.AuthResponse;
import com.abhishek.expense.dto.LoginRequest;
import com.abhishek.expense.dto.SignupRequest;
import com.abhishek.expense.error.ApiException;
import com.abhishek.expense.repository.UserRepository;
import com.abhishek.expense.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void signsUpWithNormalizedEmailAndHashedPassword() {
        when(userRepository.existsByEmail("abhi@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("bcrypt-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            ReflectionTestUtils.invokeMethod(user, "prePersist");
            return user;
        });
        when(jwtService.createToken(any(User.class))).thenReturn("access-token");

        AuthResponse response = authService.signup(
            new SignupRequest("Abhishek", " Abhi@Example.com ", "secret123")
        );

        assertThat(response.user().email()).isEqualTo("abhi@example.com");
        assertThat(response.token()).isEqualTo("access-token");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("bcrypt-hash");
    }

    @Test
    void rejectsInvalidLoginCredentials() {
        User user = User.create("Abhishek", "abhi@example.com", "bcrypt-hash");
        when(userRepository.findByEmail("abhi@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "bcrypt-hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("abhi@example.com", "wrong-password")))
            .isInstanceOf(ApiException.class)
            .hasMessage("Invalid email or password");
    }
}
