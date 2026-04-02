package com.wdd.shakeeat.module.shake.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wdd.shakeeat.common.constant.UserConstants;
import com.wdd.shakeeat.common.exception.BusinessException;
import com.wdd.shakeeat.common.util.JsonArrayUtils;
import com.wdd.shakeeat.module.food.entity.FoodItem;
import com.wdd.shakeeat.module.food.mapper.FoodItemMapper;
import com.wdd.shakeeat.module.history.entity.ShakeHistory;
import com.wdd.shakeeat.module.history.enums.AiSource;
import com.wdd.shakeeat.module.history.enums.ResultStatus;
import com.wdd.shakeeat.module.history.mapper.ShakeHistoryMapper;
import com.wdd.shakeeat.module.preference.entity.UserPreference;
import com.wdd.shakeeat.module.preference.mapper.UserPreferenceMapper;
import com.wdd.shakeeat.module.shake.dto.ShakeRequest;
import com.wdd.shakeeat.module.shake.dto.ShakeQuotaResponse;
import com.wdd.shakeeat.module.shake.dto.ShakeResponse;
import com.wdd.shakeeat.module.shake.dto.ScoreDetailResponse;
import com.wdd.shakeeat.module.shake.enums.RejectFeedbackCode;
import com.wdd.shakeeat.module.shake.enums.ShakeMode;
import com.wdd.shakeeat.module.shake.service.ShakeService;
import com.wdd.shakeeat.module.tag.entity.FoodItemTagRel;
import com.wdd.shakeeat.module.tag.entity.FoodTag;
import com.wdd.shakeeat.module.tag.mapper.FoodItemTagRelMapper;
import com.wdd.shakeeat.module.tag.mapper.FoodTagMapper;
import com.wdd.shakeeat.support.ai.AiTipService;
import com.wdd.shakeeat.support.order.OrderLinkResolver;
import com.wdd.shakeeat.support.template.FallbackTipBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShakeServiceImpl implements ShakeService {

    private static final int MAX_DAILY_SHAKE_COUNT = 30;
    private static final int RECENT_HISTORY_LIMIT = 7;
    private static final int RECENT_STRONG_WINDOW = 3;
    private static final int MIN_MODE_HIT_COUNT = 3;
    private static final double RECENT_STRONG_FACTOR = 0.35D;
    private static final double RECENT_WEAK_FACTOR = 0.65D;

    private final FoodItemMapper foodItemMapper;
    private final FoodItemTagRelMapper foodItemTagRelMapper;
    private final UserPreferenceMapper userPreferenceMapper;
    private final ShakeHistoryMapper shakeHistoryMapper;
    private final FoodTagMapper foodTagMapper;
    private final JsonArrayUtils jsonArrayUtils;
    private final AiTipService aiTipService;
    private final OrderLinkResolver orderLinkResolver;
    private final FallbackTipBuilder fallbackTipBuilder;

    @Override
    @Transactional
    public ShakeResponse shake(ShakeRequest request) {
        validateDailyCount();
        UserPreference preference = loadPreference();
        List<FoodItem> foods = listEnabledFoods();
        List<Long> recentFoodIds = listRecentFoodIds();
        Map<Long, List<Long>> tagMap = buildTagMap();
        Map<Long, String> tagNameMap = buildTagNameMap();
        List<Candidate> candidates = buildCandidates(foods, tagMap, preference, recentFoodIds, request.getMode());
        Candidate selected = pickCandidate(candidates);
        ShakeHistory history = saveHistory(request, selected, preference, tagNameMap);
        return toResponse(history, selected, tagMap);
    }

    @Override
    public void accept(Long historyId) {
        updateStatus(historyId, ResultStatus.ACCEPTED, true, null);
    }

    @Override
    public void reject(Long historyId, String rejectFeedbackCode) {
        updateStatus(historyId, ResultStatus.REJECTED, false, RejectFeedbackCode.fromCode(rejectFeedbackCode).name());
    }

    @Override
    public ShakeQuotaResponse getQuota() {
        long usedCount = countTodayShake();
        return ShakeQuotaResponse.builder()
            .dailyLimit(MAX_DAILY_SHAKE_COUNT)
            .usedCount(usedCount)
            .remainingCount(Math.max(0, MAX_DAILY_SHAKE_COUNT - usedCount))
            .build();
    }

    private void validateDailyCount() {
        long usedCount = countTodayShake();
        // 先用数据库计数兜住每日上限, 等 Redis 接入后再切到缓存限流。
        if (usedCount >= MAX_DAILY_SHAKE_COUNT) {
            throw new BusinessException("今日摇一摇次数已达上限");
        }
    }

    private long countTodayShake() {
        LambdaQueryWrapper<ShakeHistory> query = new LambdaQueryWrapper<>();
        query.eq(ShakeHistory::getUserId, UserConstants.DEFAULT_USER_ID);
        query.between(ShakeHistory::getShakeTime, startOfToday(), endOfToday());
        return shakeHistoryMapper.selectCount(query);
    }

    private UserPreference loadPreference() {
        LambdaQueryWrapper<UserPreference> query = new LambdaQueryWrapper<>();
        query.eq(UserPreference::getUserId, UserConstants.DEFAULT_USER_ID);
        UserPreference preference = userPreferenceMapper.selectOne(query);
        if (preference != null) {
            return preference;
        }
        throw new BusinessException("用户偏好初始化数据不存在");
    }

    private List<FoodItem> listEnabledFoods() {
        LambdaQueryWrapper<FoodItem> query = new LambdaQueryWrapper<>();
        query.eq(FoodItem::getDeleted, 0);
        query.eq(FoodItem::getEnabled, 1);
        List<FoodItem> foods = foodItemMapper.selectList(query);
        if (!foods.isEmpty()) {
            return foods;
        }
        throw new BusinessException("当前菜单库没有可推荐的食物");
    }

    private List<Long> listRecentFoodIds() {
        LambdaQueryWrapper<ShakeHistory> query = new LambdaQueryWrapper<>();
        query.eq(ShakeHistory::getUserId, UserConstants.DEFAULT_USER_ID);
        query.orderByDesc(ShakeHistory::getShakeTime, ShakeHistory::getId);
        query.last("LIMIT " + RECENT_HISTORY_LIMIT);
        // 最近 7 次结果只用于多样性修正, 不参与业务状态过滤。
        return shakeHistoryMapper.selectList(query).stream()
            .map(ShakeHistory::getFoodId)
            .filter(foodId -> foodId != null)
            .toList();
    }

    private Map<Long, List<Long>> buildTagMap() {
        LambdaQueryWrapper<FoodItemTagRel> query = new LambdaQueryWrapper<>();
        return foodItemTagRelMapper.selectList(query).stream()
            .collect(java.util.stream.Collectors.groupingBy(
                FoodItemTagRel::getFoodId,
                java.util.stream.Collectors.mapping(FoodItemTagRel::getTagId, java.util.stream.Collectors.toList())
            ));
    }

    private Map<Long, String> buildTagNameMap() {
        LambdaQueryWrapper<FoodTag> query = new LambdaQueryWrapper<>();
        query.eq(FoodTag::getDeleted, 0);
        return foodTagMapper.selectList(query).stream()
            .collect(java.util.stream.Collectors.toMap(FoodTag::getId, FoodTag::getName));
    }

    private List<Candidate> buildCandidates(
        List<FoodItem> foods,
        Map<Long, List<Long>> tagMap,
        UserPreference preference,
        List<Long> recentFoodIds,
        ShakeMode mode
    ) {
        List<Long> favoriteIds = jsonArrayUtils.parseLongList(preference.getFavoriteTagIds());
        List<Long> avoidIds = jsonArrayUtils.parseLongList(preference.getAvoidTagIds());
        List<Candidate> base = foods.stream()
            .map(food -> toCandidate(
                food,
                tagMap.getOrDefault(food.getId(), Collections.emptyList()),
                favoriteIds,
                avoidIds,
                preference.getMaxBudget(),
                recentFoodIds,
                mode
            ))
            .filter(Candidate::available)
            .toList();
        List<Candidate> modeHits = base.stream().filter(Candidate::modeHit).toList();
        List<Candidate> candidates = selectCandidatePool(base, modeHits, recentFoodIds);
        if (!candidates.isEmpty()) {
            return candidates;
        }
        throw new BusinessException("当前菜单库没有可推荐的食物, 请先添加菜单或调整偏好");
    }

    private Candidate toCandidate(
        FoodItem food,
        List<Long> tagIds,
        List<Long> favoriteIds,
        List<Long> avoidIds,
        BigDecimal maxBudget,
        List<Long> recentFoodIds,
        ShakeMode mode
    ) {
        // 避免标签优先级最高, 命中后直接排除, 不再进入权重计算。
        if (tagIds.stream().anyMatch(avoidIds::contains)) {
            return Candidate.unavailable(food, tagIds);
        }
        boolean modeHit = isModeHit(mode, tagIds);
        boolean favoriteHit = tagIds.stream().anyMatch(favoriteIds::contains);
        double budgetFactor = budgetFactor(food.getReferencePrice(), maxBudget);
        double diversityFactor = diversityFactor(food.getId(), recentFoodIds);
        double score = food.getWeight();
        score = score * (modeHit ? 1.8D : 1D);
        score = score * (favoriteHit ? 1.3D : 1D);
        score = score * budgetFactor;
        score = score * diversityFactor;
        return Candidate.available(food, tagIds, modeHit, favoriteHit, budgetFactor, diversityFactor, score);
    }

    private List<Candidate> selectCandidatePool(
        List<Candidate> base,
        List<Candidate> modeHits,
        List<Long> recentFoodIds
    ) {
        // 当前模式候选太少时, 放宽到全量池并保留模式权重, 避免页面长期只在 1-2 道菜里打转。
        List<Candidate> preferred = modeHits.size() >= MIN_MODE_HIT_COUNT ? modeHits : base;
        List<Candidate> latestFiltered = excludeLatestFood(preferred, recentFoodIds);
        if (!latestFiltered.isEmpty()) {
            return latestFiltered;
        }
        return preferred;
    }

    private List<Candidate> excludeLatestFood(List<Candidate> candidates, List<Long> recentFoodIds) {
        if (recentFoodIds.isEmpty()) {
            return candidates;
        }
        Long latestFoodId = recentFoodIds.get(0);
        List<Candidate> filtered = candidates.stream()
            .filter(candidate -> !latestFoodId.equals(candidate.food().getId()))
            .toList();
        return filtered.isEmpty() ? candidates : filtered;
    }

    private boolean isModeHit(ShakeMode mode, List<Long> tagIds) {
        return switch (mode) {
            case LAZY -> tagIds.contains(1L);
            case CLEAR -> tagIds.contains(2L);
            case SPICY -> tagIds.contains(3L);
            case LIGHT -> tagIds.contains(4L) || tagIds.contains(5L);
        };
    }

    private double budgetFactor(BigDecimal price, BigDecimal budget) {
        if (price == null || budget == null) {
            return 1D;
        }
        return price.compareTo(budget) > 0 ? 0.85D : 1D;
    }

    private double diversityFactor(Long foodId, List<Long> recentFoodIds) {
        boolean inRecentThree = recentFoodIds.stream()
            .limit(RECENT_STRONG_WINDOW)
            .anyMatch(recentFoodId -> recentFoodId.equals(foodId));
        if (inRecentThree) {
            return RECENT_STRONG_FACTOR;
        }
        boolean inRecentSeven = recentFoodIds.stream().anyMatch(recentFoodId -> recentFoodId.equals(foodId));
        if (inRecentSeven) {
            return RECENT_WEAK_FACTOR;
        }
        return 1D;
    }

    private Candidate pickCandidate(List<Candidate> candidates) {
        double total = candidates.stream().mapToDouble(Candidate::score).sum();
        double random = ThreadLocalRandom.current().nextDouble(total);
        double current = 0D;
        for (Candidate candidate : candidates) {
            current += candidate.score();
            if (random <= current) {
                return candidate;
            }
        }
        return candidates.get(candidates.size() - 1);
    }

    private ShakeHistory saveHistory(
        ShakeRequest request,
        Candidate selected,
        UserPreference preference,
        Map<Long, String> tagNameMap
    ) {
        ShakeHistory history = new ShakeHistory();
        history.setUserId(UserConstants.DEFAULT_USER_ID);
        history.setFoodId(selected.food().getId());
        history.setFoodName(selected.food().getName());
        history.setMode(request.getMode().name());
        history.setTriggerType(request.getTriggerType().name());
        history.setResultStatus(ResultStatus.PENDING.name());
        fillAiTip(history, request.getMode(), selected, preference, tagNameMap);
        history.setScoreSnapshot(buildScoreSnapshot(selected));
        fillDisplaySnapshot(history, selected, tagNameMap);
        shakeHistoryMapper.insert(history);
        return history;
    }

    private String buildScoreSnapshot(Candidate candidate) {
        return jsonArrayUtils.toJsonObject(toScoreDetail(candidate), "推荐原因快照保存失败");
    }

    private ShakeResponse toResponse(ShakeHistory history, Candidate selected, Map<Long, List<Long>> tagMap) {
        return ShakeResponse.builder()
            .historyId(history.getId())
            .foodId(selected.food().getId())
            .foodName(selected.food().getName())
            .emoji(selected.food().getEmoji())
            .mode(history.getMode())
            .aiTip(history.getAiTip())
            .aiSource(history.getAiSource())
            .steps(orderLinkResolver.resolveDisplaySteps(selected.food()))
            .referencePrice(selected.food().getReferencePrice())
            .orderUrl(orderLinkResolver.resolveDisplayUrl(selected.food()))
            .tagIds(tagMap.getOrDefault(selected.food().getId(), Collections.emptyList()))
            .scoreDetail(toScoreDetail(selected))
            .build();
    }

    private void updateStatus(Long historyId, ResultStatus status, boolean confirmed, String rejectFeedbackCode) {
        ShakeHistory history = getHistory(historyId);
        // 一条摇一摇记录只允许从 PENDING 进入一次终态, 避免前端重复点击覆盖结果。
        if (!ResultStatus.PENDING.name().equals(history.getResultStatus())) {
            throw new BusinessException("当前记录状态不可重复操作");
        }
        history.setResultStatus(status.name());
        history.setConfirmTime(confirmed ? LocalDateTime.now() : null);
        history.setRejectFeedbackCode(rejectFeedbackCode);
        shakeHistoryMapper.updateById(history);
    }

    private ShakeHistory getHistory(Long historyId) {
        ShakeHistory history = shakeHistoryMapper.selectById(historyId);
        if (history != null) {
            return history;
        }
        throw new BusinessException("历史记录不存在");
    }

    private LocalDateTime startOfToday() {
        return LocalDate.now().atStartOfDay();
    }

    private LocalDateTime endOfToday() {
        return LocalDate.now().plusDays(1).atStartOfDay().minusNanos(1);
    }

    private void fillAiTip(
        ShakeHistory history,
        ShakeMode mode,
        Candidate selected,
        UserPreference preference,
        Map<Long, String> tagNameMap
    ) {
        try {
            List<String> tagNames = toTagNames(selected.tagIds(), tagNameMap);
            List<String> favoriteTagNames = toTagNames(jsonArrayUtils.parseLongList(preference.getFavoriteTagIds()), tagNameMap);
            String aiTip = aiTipService.generateTip(selected.food(), mode, tagNames, favoriteTagNames);
            history.setAiTip(aiTip);
            history.setAiSource(AiSource.OLLAMA.name());
        } catch (Exception ex) {
            // AI 调用失败不能阻断主流程, 直接回退模板文案保证首页有结果。
            history.setAiTip(fallbackTipBuilder.build(selected.food(), mode));
            history.setAiSource(AiSource.FALLBACK.name());
        }
    }

    private void fillDisplaySnapshot(ShakeHistory history, Candidate selected, Map<Long, String> tagNameMap) {
        history.setStepsSnapshot(orderLinkResolver.resolveDisplaySteps(selected.food()));
        history.setOrderUrlSnapshot(orderLinkResolver.resolveDisplayUrl(selected.food()));
        history.setReferencePriceSnapshot(selected.food().getReferencePrice());
        history.setTagSnapshot(jsonArrayUtils.toStringJson(toTagNames(selected.tagIds(), tagNameMap)));
    }

    private List<String> toTagNames(List<Long> tagIds, Map<Long, String> tagNameMap) {
        return tagIds.stream()
            .map(tagNameMap::get)
            .filter(name -> name != null && !name.isBlank())
            .toList();
    }

    private ScoreDetailResponse toScoreDetail(Candidate candidate) {
        return ScoreDetailResponse.builder()
            .baseWeight(BigDecimal.valueOf(candidate.food().getWeight()))
            .modeHit(candidate.modeHit())
            .modeFactor(normalizeDecimal(candidate.modeFactor()))
            .favoriteHit(candidate.favoriteHit())
            .favoriteFactor(normalizeDecimal(candidate.favoriteFactor()))
            .budgetFactor(normalizeDecimal(candidate.budgetFactor()))
            .diversityFactor(normalizeDecimal(candidate.diversityFactor()))
            .finalScore(normalizeDecimal(candidate.score()))
            .reasonLabels(buildReasonLabels(candidate))
            .build();
    }

    private List<String> buildReasonLabels(Candidate candidate) {
        List<String> labels = new ArrayList<>();
        labels.add(candidate.modeHit() ? "命中当前模式, 权重 x1.80" : "未命中当前模式, 不加权");
        labels.add(candidate.favoriteHit() ? "命中喜欢标签, 权重 x1.30" : "未命中喜欢标签, 不加权");
        labels.add(candidate.budgetFactor() < 1D ? "超出预算, 权重 x0.85" : "预算友好, 不降权");
        if (candidate.diversityFactor() == RECENT_STRONG_FACTOR) {
            labels.add("最近 3 次出现过, 权重 x0.35");
        } else if (candidate.diversityFactor() == RECENT_WEAK_FACTOR) {
            labels.add("最近 7 次出现过, 权重 x0.65");
        } else {
            labels.add("近期未重复出现, 不降权");
        }
        return labels;
    }

    private BigDecimal normalizeDecimal(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    private record Candidate(
        FoodItem food,
        List<Long> tagIds,
        boolean modeHit,
        boolean favoriteHit,
        boolean available,
        double budgetFactor,
        double diversityFactor,
        double score
    ) {

        private static Candidate unavailable(FoodItem food, List<Long> tagIds) {
            return new Candidate(food, tagIds, false, false, false, 1D, 1D, 0D);
        }

        private static Candidate available(
            FoodItem food,
            List<Long> tagIds,
            boolean modeHit,
            boolean favoriteHit,
            double budgetFactor,
            double diversityFactor,
            double score
        ) {
            return new Candidate(food, new ArrayList<>(tagIds), modeHit, favoriteHit, true, budgetFactor, diversityFactor, score);
        }

        private double modeFactor() {
            return modeHit ? 1.8D : 1D;
        }

        private double favoriteFactor() {
            return favoriteHit ? 1.3D : 1D;
        }
    }
}
