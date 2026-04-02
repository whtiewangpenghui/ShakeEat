package com.wdd.shakeeat.support.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wdd.shakeeat.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class RestOllamaGenerateClient implements OllamaGenerateClient {

    @Override
    public String generate(String baseUrl, String model, String prompt, double temperature) {
        RestClient client = RestClient.builder().baseUrl(baseUrl).build();
        GenerateResponse response = client.post()
            .uri("/api/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new GenerateRequest(model, prompt, false, new Options(temperature)))
            .retrieve()
            .body(GenerateResponse.class);
        String content = response == null ? null : response.response();
        if (StringUtils.hasText(content)) {
            return content.trim();
        }
        throw new BusinessException("Ollama 返回内容为空");
    }

    private record GenerateRequest(
        String model,
        String prompt,
        boolean stream,
        Options options
    ) {
    }

    private record Options(double temperature) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GenerateResponse(String response) {
    }
}
