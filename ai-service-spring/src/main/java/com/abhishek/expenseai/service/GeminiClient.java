package com.abhishek.expenseai.service;

public interface GeminiClient {

    String generate(String systemInstruction, String prompt);
}
