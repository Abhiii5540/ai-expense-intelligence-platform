package com.abhishek.expenseai.controller;

import com.abhishek.expenseai.dto.InsightsRequest;
import com.abhishek.expenseai.dto.InsightsResponse;
import com.abhishek.expenseai.service.GeminiInsightsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class InsightsController {

    private final GeminiInsightsService insightsService;

    public InsightsController(GeminiInsightsService insightsService) {
        this.insightsService = insightsService;
    }

    @PostMapping("/insights")
    InsightsResponse generate(@Valid @RequestBody InsightsRequest request) {
        return new InsightsResponse(insightsService.generate(request.expenses()));
    }
}
