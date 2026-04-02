package com.wdd.shakeeat.module.food.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FoodExportItemResponse {

    private String name;
    private String emoji;
    private String steps;
    private String imageUrl;
    private String orderUrl;
    private Integer weight;
    private BigDecimal referencePrice;
    private Integer enabled;
    private List<Long> tagIds;
}
