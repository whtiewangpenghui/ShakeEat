package com.wdd.shakeeat.module.history.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistoryPageResponse {

    private long total;
    private List<HistoryListItemResponse> records;
}
