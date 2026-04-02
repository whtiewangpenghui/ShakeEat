package com.wdd.shakeeat.support.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.wdd.shakeeat.common.exception.BusinessException;
import com.wdd.shakeeat.module.food.entity.FoodItem;
import com.wdd.shakeeat.module.shake.enums.ShakeMode;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OllamaAiTipServiceTest {

    @Mock
    private OllamaGenerateClient ollamaGenerateClient;

    private OllamaProperties properties;
    private OllamaAiTipService ollamaAiTipService;

    @BeforeEach
    void setUp() {
        properties = new OllamaProperties();
        properties.setBaseUrl("http://127.0.0.1:11434");
        properties.setTextModels(List.of("deepseek-v3.1:671b-cloud", "glm-4.6:cloud"));
        ollamaAiTipService = new OllamaAiTipService(properties, ollamaGenerateClient);
    }

    @Test
    void generateTipShouldUsePrimaryModelWhenFirstCallSucceeds() {
        when(ollamaGenerateClient.generate(eq("http://127.0.0.1:11434"), eq("deepseek-v3.1:671b-cloud"), anyString(), anyDouble()))
            .thenReturn("今晚就吃麻辣香锅, 香辣直接, 不用再为晚饭继续拉扯。");

        String tip = ollamaAiTipService.generateTip(buildFood(), ShakeMode.LAZY, List.of("外卖"), List.of("外卖"));

        assertEquals("今晚就吃麻辣香锅, 香辣直接, 不用再为晚饭继续拉扯。", tip);
    }

    @Test
    void generateTipShouldFallbackToNextModelWhenPrimaryFails() {
        when(ollamaGenerateClient.generate(eq("http://127.0.0.1:11434"), eq("deepseek-v3.1:671b-cloud"), anyString(), anyDouble()))
            .thenThrow(new RuntimeException("timeout"));
        when(ollamaGenerateClient.generate(eq("http://127.0.0.1:11434"), eq("glm-4.6:cloud"), anyString(), anyDouble()))
            .thenReturn("今晚来份麻辣香锅外卖, 下单快, 香味足, 很适合下班后的你。");

        String tip = ollamaAiTipService.generateTip(buildFood(), ShakeMode.LAZY, List.of("外卖"), List.of("外卖"));

        assertEquals("今晚来份麻辣香锅外卖, 下单快, 香味足, 很适合下班后的你。", tip);
        InOrder inOrder = inOrder(ollamaGenerateClient);
        inOrder.verify(ollamaGenerateClient).generate(eq("http://127.0.0.1:11434"), eq("deepseek-v3.1:671b-cloud"), anyString(), anyDouble());
        inOrder.verify(ollamaGenerateClient).generate(eq("http://127.0.0.1:11434"), eq("glm-4.6:cloud"), anyString(), anyDouble());
    }

    @Test
    void generateTipShouldThrowBusinessExceptionWhenAllModelsFail() {
        when(ollamaGenerateClient.generate(eq("http://127.0.0.1:11434"), anyString(), anyString(), anyDouble()))
            .thenThrow(new RuntimeException("network"));

        assertThrows(BusinessException.class, () ->
            ollamaAiTipService.generateTip(buildFood(), ShakeMode.LAZY, List.of("外卖"), List.of("外卖"))
        );
        org.mockito.Mockito.verify(ollamaGenerateClient, times(2))
            .generate(eq("http://127.0.0.1:11434"), anyString(), anyString(), anyDouble());
    }

    private FoodItem buildFood() {
        FoodItem food = new FoodItem();
        food.setName("麻辣香锅外卖");
        food.setSteps("1. 打开外卖 App 2. 搜索麻辣香锅 3. 下单");
        return food;
    }
}
