package com.wdd.shakeeat.support.ai;

import com.wdd.shakeeat.common.exception.BusinessException;
import com.wdd.shakeeat.module.food.entity.FoodItem;
import com.wdd.shakeeat.module.shake.enums.ShakeMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OllamaAiTipService implements AiTipService {

    private final OllamaProperties properties;
    private final OllamaGenerateClient ollamaGenerateClient;

    @Override
    public String generateTip(FoodItem food, ShakeMode mode, List<String> tagNames, List<String> favoriteTagNames) {
        if (!properties.isEnabled()) {
            throw new BusinessException("Ollama 未启用");
        }
        String prompt = buildPrompt(food, mode, tagNames, favoriteTagNames);
        Exception lastException = null;
        for (String model : properties.resolveTextModels()) {
            try {
                return ollamaGenerateClient.generate(properties.getBaseUrl(), model, prompt, properties.getTemperature());
            } catch (Exception ex) {
                // cloud 模型偶发超时或限流时, 顺序切到下一个文本模型继续兜底。
                lastException = ex;
                log.warn("Ollama 模型调用失败, model={}, reason={}", model, ex.getMessage());
            }
        }
        throw buildFailure(lastException);
    }

    private String buildPrompt(
        FoodItem food,
        ShakeMode mode,
        List<String> tagNames,
        List<String> favoriteTagNames
    ) {
        return "你是晚饭推荐助手。"
            + "请只输出一句中文推荐文案, 长度 40 到 80 字, 语气轻松温和, 不要 Markdown, 不要编号。"
            + "不要复述步骤, 不要写打开 App、搜索、下单之类的操作说明, 只说为什么现在适合吃它。"
            + "\n模式: " + mode.name()
            + "\n食物: " + food.getName()
            + "\n标签: " + String.join(", ", tagNames)
            + "\n喜欢标签: " + String.join(", ", favoriteTagNames)
            + "\n参考价格: " + safePrice(food.getReferencePrice())
            + "\n基础步骤: " + safeText(food.getSteps());
    }

    private String safeText(String text) {
        return text == null ? "无" : text;
    }

    private String safePrice(java.math.BigDecimal price) {
        return price == null ? "未知" : price.stripTrailingZeros().toPlainString() + "元";
    }

    private BusinessException buildFailure(Exception lastException) {
        if (lastException instanceof BusinessException businessException) {
            return businessException;
        }
        return new BusinessException("Ollama 文案生成失败");
    }
}
