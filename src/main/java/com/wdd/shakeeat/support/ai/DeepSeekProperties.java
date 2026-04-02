package com.wdd.shakeeat.support.ai;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.ai.deepseek")
public class DeepSeekProperties {

    private boolean enabled;
    private String apiKey;
    private String baseUrl = "https://api.deepseek.com";
    private String model = "deepseek-chat";
    private double temperature = 0.75D;
    private int maxTokens = 200;
}
