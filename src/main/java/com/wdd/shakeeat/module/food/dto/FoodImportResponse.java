package com.wdd.shakeeat.module.food.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FoodImportResponse {

    private int createdCount;
    private int updatedCount;
    private int skippedCount;
}
