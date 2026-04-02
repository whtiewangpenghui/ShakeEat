package com.wdd.shakeeat.module.shake.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScoreDetailResponse {

    private BigDecimal baseWeight;
    private boolean modeHit;
    private BigDecimal modeFactor;
    private boolean favoriteHit;
    private BigDecimal favoriteFactor;
    private BigDecimal budgetFactor;
    private BigDecimal diversityFactor;
    private BigDecimal finalScore;
    private List<String> reasonLabels;
}
