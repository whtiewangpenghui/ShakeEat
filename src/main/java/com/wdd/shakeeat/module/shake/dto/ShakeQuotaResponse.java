package com.wdd.shakeeat.module.shake.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShakeQuotaResponse {

    private int dailyLimit;
    private long usedCount;
    private long remainingCount;
}
