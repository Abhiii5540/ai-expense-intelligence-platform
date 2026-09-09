package com.abhishek.expense.service;

import com.abhishek.expense.dto.AiInsightsRequest;
import com.abhishek.expense.dto.InsightsResponse;

public interface AiInsightsClient {

    InsightsResponse generate(AiInsightsRequest request);
}
