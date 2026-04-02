package com.wdd.shakeeat.module.history.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import com.wdd.shakeeat.module.shake.dto.ScoreDetailResponse;

@Getter
@Builder
public class HistoryDetailResponse {

    private Long id;
    private String foodName;
    private String mode;
    private String triggerType;
    private String resultStatus;
    private String aiTip;
    private String aiSource;
    private String steps;
    private BigDecimal referencePrice;
    private String orderUrl;
    private List<String> tagNames;
    private ScoreDetailResponse scoreDetail;
    private String rejectFeedbackCode;
    private String rejectFeedbackLabel;
    private LocalDateTime shakeTime;
    private LocalDateTime confirmTime;
}
