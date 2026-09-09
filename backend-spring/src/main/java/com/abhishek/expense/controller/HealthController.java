package com.abhishek.expense.controller;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    Map<String, Object> health() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "ok");
        response.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0);
        response.put("timestamp", Instant.now().toString());
        return response;
    }
}
