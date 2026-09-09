package com.abhishek.expenseai.config;

import java.net.http.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient geminiRestClient(GeminiProperties properties) {
        HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(properties.getConnectTimeout())
            .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(properties.getReadTimeout());

        return RestClient.builder()
            .baseUrl(properties.getBaseUrl().toString())
            .requestFactory(requestFactory)
            .build();
    }
}
