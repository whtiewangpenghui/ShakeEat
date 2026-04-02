package com.wdd.shakeeat.module.preference.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PreferenceResponse {

    private Long userId;
    private List<Long> favoriteTagIds;
    private List<Long> avoidTagIds;
    private BigDecimal maxBudget;
    private String lastMode;
}
