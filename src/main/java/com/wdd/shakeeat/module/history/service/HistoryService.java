package com.wdd.shakeeat.module.history.service;

import com.wdd.shakeeat.module.history.dto.HistoryDetailResponse;
import com.wdd.shakeeat.module.history.dto.HistoryPageResponse;
import com.wdd.shakeeat.module.history.dto.HistoryStatsResponse;

public interface HistoryService {

    HistoryPageResponse listHistories(String status, long pageNo, long pageSize);

    HistoryDetailResponse getHistory(Long id);

    HistoryStatsResponse getStats();
}
