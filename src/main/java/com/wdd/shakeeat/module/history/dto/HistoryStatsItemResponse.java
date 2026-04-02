package com.wdd.shakeeat.module.history.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistoryStatsItemResponse {

    private String code;
    private String label;
    private long count;
}
