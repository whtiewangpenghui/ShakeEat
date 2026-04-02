package com.wdd.shakeeat.support.order;

import com.wdd.shakeeat.module.food.entity.FoodItem;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;

@Component
public class OrderLinkResolver {

    private static final String MEITUAN_SEARCH_PREFIX = "https://waimai.meituan.com/?keyword=";

    public String resolvePersistedUrl(String foodName, String rawUrl, String steps, BigDecimal referencePrice) {
        if (hasManualUrl(rawUrl)) {
            return rawUrl.trim();
        }
        if (!shouldExposeSearchUrl(foodName, rawUrl, steps)) {
            return null;
        }
        return buildSearchUrl(foodName, referencePrice);
    }

    public String resolveDisplayUrl(FoodItem food) {
        if (food == null) {
            return null;
        }
        return resolvePersistedUrl(food.getName(), food.getOrderUrl(), food.getSteps(), food.getReferencePrice());
    }

    public String resolveDisplaySteps(FoodItem food) {
        if (food == null) {
            return null;
        }
        if (hasManualUrl(food.getOrderUrl())) {
            return buildManualOrderSteps(food.getName(), food.getReferencePrice());
        }
        if (shouldExposeSearchUrl(food.getName(), food.getOrderUrl(), food.getSteps())) {
            return buildSearchSteps(food.getName(), food.getReferencePrice());
        }
        return food.getSteps();
    }

    private String buildSearchUrl(String foodName, BigDecimal referencePrice) {
        String keyword = buildSearchKeyword(foodName, referencePrice);
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8).replace("+", "%20");
        return MEITUAN_SEARCH_PREFIX + encodedKeyword;
    }

    private String buildSearchKeyword(String foodName, BigDecimal referencePrice) {
        String normalizedName = safeText(foodName);
        if (referencePrice == null) {
            return normalizedName;
        }
        return normalizedName + " " + formatPriceKeyword(referencePrice);
    }

    private String buildSearchSteps(String foodName, BigDecimal referencePrice) {
        String keyword = buildSearchKeyword(foodName, referencePrice);
        return "1. 点击下方美团搜索入口 2. 搜索\"" + keyword + "\" 3. 对比店铺价格后下单";
    }

    private String buildManualOrderSteps(String foodName, BigDecimal referencePrice) {
        String priceText = referencePrice == null ? "店铺价格" : "参考价 " + formatPriceKeyword(referencePrice);
        return "1. 点击下方下单入口 2. 核对\"" + safeText(foodName) + "\"和" + priceText + " 3. 确认后下单";
    }

    private String formatPriceKeyword(BigDecimal referencePrice) {
        return referencePrice.stripTrailingZeros().toPlainString() + "元";
    }

    private boolean shouldExposeSearchUrl(String foodName, String rawUrl, String steps) {
        return isMeituanSearchUrl(rawUrl) || containsTakeoutHint(foodName) || containsTakeoutHint(steps);
    }

    private boolean hasManualUrl(String rawUrl) {
        return hasText(rawUrl) && !isMeituanSearchUrl(rawUrl);
    }

    private boolean isMeituanSearchUrl(String rawUrl) {
        return hasText(rawUrl) && rawUrl.trim().startsWith(MEITUAN_SEARCH_PREFIX);
    }

    private boolean containsTakeoutHint(String text) {
        if (!hasText(text)) {
            return false;
        }
        String normalized = text.trim();
        return normalized.contains("外卖")
            || normalized.contains("下单")
            || normalized.contains("美团")
            || normalized.contains("饿了么");
    }

    private boolean hasText(String text) {
        return text != null && !text.isBlank();
    }

    private String safeText(String text) {
        return text == null ? "" : text.trim();
    }
}
