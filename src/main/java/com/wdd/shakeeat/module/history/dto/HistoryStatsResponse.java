package com.wdd.shakeeat.module.history.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistoryStatsResponse {

    private long totalCount;
    private long acceptedCount;
    private long rejectedCount;
    private long pendingCount;
    private BigDecimal acceptanceRate;
    private List<HistoryStatsItemResponse> modeStats;
    private List<HistoryStatsItemResponse> rejectFeedbackStats;
    private List<DailyShakeStatResponse> recentDailyStats;
}
