package com.wdd.shakeeat.module.history.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistoryListItemResponse {

    private Long id;
    private String foodName;
    private String mode;
    private String aiTipPreview;
    private String resultStatus;
    private LocalDateTime shakeTime;
}
