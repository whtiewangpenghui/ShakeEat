package com.wdd.shakeeat.module.history.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyShakeStatResponse {

    private String day;
    private long count;
}
