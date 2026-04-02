package com.wdd.shakeeat.module.shake.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShakeResponse {

    private Long historyId;
    private Long foodId;
    private String foodName;
    private String emoji;
    private String mode;
    private String aiTip;
    private String aiSource;
    private String steps;
    private BigDecimal referencePrice;
    private String orderUrl;
    private List<Long> tagIds;
    private ScoreDetailResponse scoreDetail;
}
