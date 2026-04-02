package com.wdd.shakeeat.support.template;

import com.wdd.shakeeat.module.food.entity.FoodItem;
import com.wdd.shakeeat.module.shake.enums.ShakeMode;
import org.springframework.stereotype.Component;

@Component
public class FallbackTipBuilder {

    public String build(FoodItem food, ShakeMode mode) {
        return "今晚吃" + food.getName() + ", " + modeText(mode) + ", 别再纠结了, 现在执行最省脑子。";
    }

    private String modeText(ShakeMode mode) {
        return switch (mode) {
            case LAZY -> "下单快, 决策成本低";
            case CLEAR -> "顺手消耗库存, 冰箱压力小一点";
            case SPICY -> "有点刺激, 比较解馋";
            case LIGHT -> "负担轻一点, 吃完更舒服";
        };
    }
}
