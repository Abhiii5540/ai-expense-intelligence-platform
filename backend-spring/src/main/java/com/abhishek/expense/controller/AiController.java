package com.abhishek.expense.controller;

import com.abhishek.expense.dto.InsightsResponse;
import com.abhishek.expense.security.UserPrincipal;
import com.abhishek.expense.service.AiInsightsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiInsightsService aiInsightsService;

    public AiController(AiInsightsService aiInsightsService) {
        this.aiInsightsService = aiInsightsService;
    }

    @GetMapping("/insights")
    InsightsResponse getInsights(@AuthenticationPrincipal UserPrincipal principal) {
        return aiInsightsService.generate(principal.id());
    }
}
