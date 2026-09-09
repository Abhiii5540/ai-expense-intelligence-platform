package com.abhishek.expenseai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.abhishek.expenseai.config.GeminiProperties;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class RestGeminiClientTest {

    @Test
    void retriesTransientProviderFailure() {
        GeminiProperties properties = new GeminiProperties();
        properties.setApiKey("test-key");
        properties.setModel("gemini-test");
        properties.setBaseUrl(URI.create("https://gemini.test"));

        RestClient.Builder builder = RestClient.builder().baseUrl(properties.getBaseUrl().toString());
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        String endpoint = "https://gemini.test/v1beta/models/gemini-test:generateContent";
        server.expect(once(), requestTo(endpoint)).andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
        server.expect(once(), requestTo(endpoint)).andRespond(withSuccess(
            "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"[\\\"Insight with ₹100\\\"]\"}]}}]}",
            MediaType.APPLICATION_JSON
        ));

        RestGeminiClient client = new RestGeminiClient(builder.build(), properties);

        assertThat(client.generate("system", "prompt")).isEqualTo("[\"Insight with ₹100\"]");
        server.verify();
    }
}
