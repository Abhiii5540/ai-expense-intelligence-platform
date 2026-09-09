package com.abhishek.expense.controller;

import com.abhishek.expense.dto.ApiSuccess;
import com.abhishek.expense.dto.AuthResponse;
import com.abhishek.expense.dto.LoginRequest;
import com.abhishek.expense.dto.SignupRequest;
import com.abhishek.expense.dto.UserResponse;
import com.abhishek.expense.security.UserPrincipal;
import com.abhishek.expense.service.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    ResponseEntity<ApiSuccess<AuthResponse>> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiSuccess.of(authService.signup(request)));
    }

    @PostMapping("/login")
    ApiSuccess<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiSuccess.of(authService.login(request));
    }

    @GetMapping("/me")
    ApiSuccess<Map<String, UserResponse>> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiSuccess.of(Map.of("user", authService.getUser(principal.id())));
    }
}
