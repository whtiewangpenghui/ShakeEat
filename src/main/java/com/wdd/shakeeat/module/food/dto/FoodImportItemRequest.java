package com.wdd.shakeeat.module.food.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoodImportItemRequest {

    @NotBlank(message = "name 不能为空")
    private String name;

    private String emoji;
    private String steps;
    private String imageUrl;
    private String orderUrl;

    @Min(value = 0, message = "weight 不能小于 0")
    @Max(value = 1000, message = "weight 不能大于 1000")
    private Integer weight;

    private BigDecimal referencePrice;
    private Integer enabled;
    private List<Long> tagIds;
}
