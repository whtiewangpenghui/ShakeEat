package com.wdd.shakeeat.module.history.controller;

import com.wdd.shakeeat.common.api.ApiResponse;
import com.wdd.shakeeat.module.history.dto.HistoryDetailResponse;
import com.wdd.shakeeat.module.history.dto.HistoryPageResponse;
import com.wdd.shakeeat.module.history.dto.HistoryStatsResponse;
import com.wdd.shakeeat.module.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/histories")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    public ApiResponse<HistoryPageResponse> listHistories(
        @RequestParam(required = false) String status,
        @RequestParam(defaultValue = "1") long pageNo,
        @RequestParam(defaultValue = "30") long pageSize
    ) {
        return ApiResponse.ok(historyService.listHistories(status, pageNo, pageSize));
    }

    @GetMapping("/stats")
    public ApiResponse<HistoryStatsResponse> getStats() {
        return ApiResponse.ok(historyService.getStats());
    }

    @GetMapping("/{id}")
    public ApiResponse<HistoryDetailResponse> getHistory(@PathVariable Long id) {
        return ApiResponse.ok(historyService.getHistory(id));
    }
}
