package com.wdd.shakeeat.support.ai;

import com.wdd.shakeeat.module.food.entity.FoodItem;
import com.wdd.shakeeat.module.shake.enums.ShakeMode;
import java.util.List;

public interface AiTipService {

    String generateTip(FoodItem food, ShakeMode mode, List<String> tagNames, List<String> favoriteTagNames);
}
