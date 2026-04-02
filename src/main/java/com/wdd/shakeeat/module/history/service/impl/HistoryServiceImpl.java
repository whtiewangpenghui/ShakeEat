package com.wdd.shakeeat.module.history.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wdd.shakeeat.common.util.JsonArrayUtils;
import com.wdd.shakeeat.common.exception.BusinessException;
import com.wdd.shakeeat.common.constant.UserConstants;
import com.wdd.shakeeat.module.history.dto.DailyShakeStatResponse;
import com.wdd.shakeeat.module.food.entity.FoodItem;
import com.wdd.shakeeat.module.food.mapper.FoodItemMapper;
import com.wdd.shakeeat.module.history.dto.HistoryDetailResponse;
import com.wdd.shakeeat.module.history.dto.HistoryListItemResponse;
import com.wdd.shakeeat.module.history.dto.HistoryPageResponse;
import com.wdd.shakeeat.module.history.dto.HistoryStatsItemResponse;
import com.wdd.shakeeat.module.history.dto.HistoryStatsResponse;
import com.wdd.shakeeat.module.history.entity.ShakeHistory;
import com.wdd.shakeeat.module.history.service.HistoryService;
import com.wdd.shakeeat.module.history.mapper.ShakeHistoryMapper;
import com.wdd.shakeeat.module.shake.enums.RejectFeedbackCode;
import com.wdd.shakeeat.support.order.OrderLinkResolver;
import com.wdd.shakeeat.module.shake.dto.ScoreDetailResponse;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final ShakeHistoryMapper shakeHistoryMapper;
    private final FoodItemMapper foodItemMapper;
    private final OrderLinkResolver orderLinkResolver;
    private final JsonArrayUtils jsonArrayUtils;

    @Override
    public HistoryPageResponse listHistories(String status, long pageNo, long pageSize) {
        Page<ShakeHistory> page = new Page<>(pageNo, pageSize);
        Page<ShakeHistory> result = shakeHistoryMapper.selectPage(page, buildPageQuery(status));
        List<HistoryListItemResponse> records = result.getRecords().stream().map(this::toItem).toList();
        return HistoryPageResponse.builder().total(result.getTotal()).records(records).build();
    }

    @Override
    public HistoryDetailResponse getHistory(Long id) {
        ShakeHistory history = shakeHistoryMapper.selectById(id);
        if (history == null) {
            throw new BusinessException("历史记录不存在");
        }
        return toDetail(history);
    }

    @Override
    public HistoryStatsResponse getStats() {
        LambdaQueryWrapper<ShakeHistory> query = new LambdaQueryWrapper<>();
        query.eq(ShakeHistory::getUserId, UserConstants.DEFAULT_USER_ID);
        List<ShakeHistory> histories = shakeHistoryMapper.selectList(query);
        long totalCount = histories.size();
        long acceptedCount = countByStatus(histories, "ACCEPTED");
        long rejectedCount = countByStatus(histories, "REJECTED");
        long pendingCount = countByStatus(histories, "PENDING");
        return HistoryStatsResponse.builder()
            .totalCount(totalCount)
            .acceptedCount(acceptedCount)
            .rejectedCount(rejectedCount)
            .pendingCount(pendingCount)
            .acceptanceRate(calculateAcceptanceRate(acceptedCount, totalCount))
            .modeStats(buildModeStats(histories))
            .rejectFeedbackStats(buildRejectFeedbackStats(histories))
            .recentDailyStats(buildRecentDailyStats(histories))
            .build();
    }

    private LambdaQueryWrapper<ShakeHistory> buildPageQuery(String status) {
        LambdaQueryWrapper<ShakeHistory> query = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            query.eq(ShakeHistory::getResultStatus, status);
        }
        query.orderByDesc(ShakeHistory::getShakeTime, ShakeHistory::getId);
        return query;
    }

    private HistoryListItemResponse toItem(ShakeHistory history) {
        return HistoryListItemResponse.builder()
            .id(history.getId())
            .foodName(history.getFoodName())
            .mode(history.getMode())
            .aiTipPreview(preview(history.getAiTip()))
            .resultStatus(history.getResultStatus())
            .shakeTime(history.getShakeTime())
            .build();
    }

    private HistoryDetailResponse toDetail(ShakeHistory history) {
        FoodItem food = foodItemMapper.selectById(history.getFoodId());
        String steps = history.getStepsSnapshot();
        String orderUrl = history.getOrderUrlSnapshot();
        java.math.BigDecimal referencePrice = history.getReferencePriceSnapshot();
        if (steps == null && food != null) {
            steps = orderLinkResolver.resolveDisplaySteps(food);
        }
        if (orderUrl == null && food != null) {
            orderUrl = orderLinkResolver.resolveDisplayUrl(food);
        }
        if (referencePrice == null && food != null) {
            referencePrice = food.getReferencePrice();
        }
        return HistoryDetailResponse.builder()
            .id(history.getId())
            .foodName(history.getFoodName())
            .mode(history.getMode())
            .triggerType(history.getTriggerType())
            .resultStatus(history.getResultStatus())
            .aiTip(history.getAiTip())
            .aiSource(history.getAiSource())
            .steps(steps)
            .referencePrice(referencePrice)
            .orderUrl(orderUrl)
            .tagNames(jsonArrayUtils.parseStringList(history.getTagSnapshot()))
            .scoreDetail(jsonArrayUtils.parseObject(history.getScoreSnapshot(), ScoreDetailResponse.class, "推荐原因快照数据格式错误"))
            .rejectFeedbackCode(history.getRejectFeedbackCode())
            .rejectFeedbackLabel(resolveRejectFeedbackLabel(history.getRejectFeedbackCode()))
            .shakeTime(history.getShakeTime())
            .confirmTime(history.getConfirmTime())
            .build();
    }

    private String resolveRejectFeedbackLabel(String rejectFeedbackCode) {
        if (rejectFeedbackCode == null || rejectFeedbackCode.isBlank()) {
            return null;
        }
        return RejectFeedbackCode.fromCode(rejectFeedbackCode).getLabel();
    }

    private long countByStatus(List<ShakeHistory> histories, String status) {
        return histories.stream()
            .filter(history -> status.equals(history.getResultStatus()))
            .count();
    }

    private java.math.BigDecimal calculateAcceptanceRate(long acceptedCount, long totalCount) {
        if (totalCount == 0) {
            return java.math.BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return java.math.BigDecimal.valueOf(acceptedCount)
            .multiply(java.math.BigDecimal.valueOf(100))
            .divide(java.math.BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP);
    }

    private List<HistoryStatsItemResponse> buildModeStats(List<ShakeHistory> histories) {
        return buildGroupedStats(histories, ShakeHistory::getMode, this::safeText);
    }

    private List<HistoryStatsItemResponse> buildRejectFeedbackStats(List<ShakeHistory> histories) {
        return buildGroupedStats(
            histories.stream()
                .filter(history -> history.getRejectFeedbackCode() != null && !history.getRejectFeedbackCode().isBlank())
                .toList(),
            ShakeHistory::getRejectFeedbackCode,
            this::resolveRejectFeedbackLabel
        );
    }

    private List<DailyShakeStatResponse> buildRecentDailyStats(List<ShakeHistory> histories) {
        Map<LocalDate, Long> dayCountMap = histories.stream()
            .filter(history -> history.getShakeTime() != null)
            .collect(java.util.stream.Collectors.groupingBy(
                history -> history.getShakeTime().toLocalDate(),
                java.util.stream.Collectors.counting()
            ));
        LocalDate start = LocalDate.now().minusDays(6);
        return java.util.stream.Stream.iterate(start, day -> day.plusDays(1))
            .limit(7)
            .map(day -> DailyShakeStatResponse.builder()
                .day(day.toString())
                .count(dayCountMap.getOrDefault(day, 0L))
                .build())
            .toList();
    }

    private List<HistoryStatsItemResponse> buildGroupedStats(
        List<ShakeHistory> histories,
        Function<ShakeHistory, String> codeExtractor,
        Function<String, String> labelResolver
    ) {
        return histories.stream()
            .map(codeExtractor)
            .filter(code -> code != null && !code.isBlank())
            .collect(java.util.stream.Collectors.groupingBy(Function.identity(), java.util.stream.Collectors.counting()))
            .entrySet()
            .stream()
            .sorted((left, right) -> Long.compare(right.getValue(), left.getValue()))
            .map(entry -> HistoryStatsItemResponse.builder()
                .code(entry.getKey())
                .label(labelResolver.apply(entry.getKey()))
                .count(entry.getValue())
                .build())
            .toList();
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private String preview(String aiTip) {
        if (aiTip == null || aiTip.length() <= 30) {
            return aiTip;
        }
        return aiTip.substring(0, 30);
    }
}
