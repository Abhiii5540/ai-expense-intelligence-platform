package com.abhishek.expense.controller;

import com.abhishek.expense.dto.AnalyticsResponse;
import com.abhishek.expense.dto.ApiSuccess;
import com.abhishek.expense.security.UserPrincipal;
import com.abhishek.expense.service.AnalyticsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    ApiSuccess<AnalyticsResponse> getAnalytics(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiSuccess.of(analyticsService.getAnalytics(principal.id()));
    }
}
