package com.abhishek.expense.service;

import com.abhishek.expense.domain.User;
import com.abhishek.expense.dto.AuthResponse;
import com.abhishek.expense.dto.LoginRequest;
import com.abhishek.expense.dto.SignupRequest;
import com.abhishek.expense.dto.UserResponse;
import com.abhishek.expense.error.ApiException;
import com.abhishek.expense.repository.UserRepository;
import com.abhishek.expense.security.JwtService;
import com.abhishek.expense.util.EmailNormalizer;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "User already exists");
        }

        String name = request.name() == null || request.name().isEmpty() ? null : request.name();
        User user = User.create(name, email, passwordEncoder.encode(request.password()));
        User saved = userRepository.save(user);
        return new AuthResponse(UserResponse.from(saved), jwtService.createToken(saved));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return new AuthResponse(UserResponse.from(user), jwtService.createToken(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        return UserResponse.from(user);
    }

    private String normalizeEmail(String email) {
        return EmailNormalizer.normalize(email);
    }
}
