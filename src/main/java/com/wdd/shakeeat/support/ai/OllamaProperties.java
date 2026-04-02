package com.wdd.shakeeat.support.ai;

import java.util.LinkedHashSet;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.ai.ollama")
public class OllamaProperties {

    private boolean enabled = true;
    private String baseUrl = "http://127.0.0.1:11434";
    // 默认和配置文件保持一致, 便于本地直接联调。
    private String model = "deepseek-v3.1:671b-cloud";
    // 晚饭推荐只走文本模型链路, 不把视觉和代码模型混进来。
    private List<String> textModels = List.of(
        "deepseek-v3.1:671b-cloud",
        "glm-4.6:cloud",
        "minimax-m2:cloud",
        "gpt-oss:20b-cloud",
        "gpt-oss:120b-cloud"
    );
    private double temperature = 0.7D;

    public List<String> resolveTextModels() {
        LinkedHashSet<String> models = new LinkedHashSet<>();
        if (textModels != null) {
            textModels.stream().filter(StringUtils::hasText).map(String::trim).forEach(models::add);
        }
        if (models.isEmpty() && StringUtils.hasText(model)) {
            models.add(model.trim());
        }
        return List.copyOf(models);
    }
}
