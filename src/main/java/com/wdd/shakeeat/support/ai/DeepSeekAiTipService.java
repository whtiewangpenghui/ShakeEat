package com.wdd.shakeeat.support.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wdd.shakeeat.common.exception.BusinessException;
import com.wdd.shakeeat.module.food.entity.FoodItem;
import com.wdd.shakeeat.module.shake.enums.ShakeMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class DeepSeekAiTipService {

    private final DeepSeekProperties properties;

    public String generateTip(FoodItem food, ShakeMode mode, List<String> tagNames, List<String> favoriteTagNames) {
        if (!properties.isEnabled() || !StringUtils.hasText(properties.getApiKey())) {
            throw new BusinessException("DeepSeek 未配置");
        }
        RestClient client = buildClient();
        ChatCompletionResponse response = client.post()
            .uri("/chat/completions")
            .contentType(MediaType.APPLICATION_JSON)
            .body(buildRequest(food, mode, tagNames, favoriteTagNames))
            .retrieve()
            .body(ChatCompletionResponse.class);
        String content = extractContent(response);
        if (StringUtils.hasText(content)) {
            return content.trim();
        }
        throw new BusinessException("DeepSeek 返回内容为空");
    }

    private RestClient buildClient() {
        return RestClient.builder()
            .baseUrl(properties.getBaseUrl())
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    private ChatCompletionRequest buildRequest(
        FoodItem food,
        ShakeMode mode,
        List<String> tagNames,
        List<String> favoriteTagNames
    ) {
        String prompt = buildPrompt(food, mode, tagNames, favoriteTagNames);
        ChatMessage system = new ChatMessage(
            "system",
            "你是一个晚饭推荐助手, 只输出一句中文推荐文案, 40 到 80 字, 语气轻松温和。不要复述步骤, 不要写打开 App、搜索、下单之类的操作说明。"
        );
        ChatMessage user = new ChatMessage("user", prompt);
        return new ChatCompletionRequest(
            properties.getModel(),
            List.of(system, user),
            properties.getTemperature(),
            properties.getMaxTokens()
        );
    }

    private String buildPrompt(
        FoodItem food,
        ShakeMode mode,
        List<String> tagNames,
        List<String> favoriteTagNames
    ) {
        return "模式: " + mode.name()
            + "\n食物: " + food.getName()
            + "\n标签: " + String.join(", ", tagNames)
            + "\n喜欢标签: " + String.join(", ", favoriteTagNames)
            + "\n参考价格: " + safePrice(food.getReferencePrice())
            + "\n基础步骤: " + safeText(food.getSteps())
            + "\n要求: 给出一句适合下班后决策的中文推荐文案, 不要 Markdown, 不要编号, 只说为什么适合现在吃它。";
    }

    private String safeText(String text) {
        return text == null ? "无" : text;
    }

    private String safePrice(java.math.BigDecimal price) {
        return price == null ? "未知" : price.stripTrailingZeros().toPlainString() + "元";
    }

    private String extractContent(ChatCompletionResponse response) {
        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            return null;
        }
        Choice first = response.choices().get(0);
        if (first.message() == null) {
            return null;
        }
        return first.message().content();
    }

    private record ChatCompletionRequest(
        String model,
        List<ChatMessage> messages,
        double temperature,
        @JsonProperty("max_tokens") int maxTokens
    ) {
    }

    private record ChatMessage(String role, String content) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ChatCompletionResponse(List<Choice> choices) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Choice(Message message) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Message(String content) {
    }
}
