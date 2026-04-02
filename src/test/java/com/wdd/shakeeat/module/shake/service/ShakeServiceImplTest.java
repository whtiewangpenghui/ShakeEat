package com.wdd.shakeeat.module.shake.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wdd.shakeeat.common.exception.BusinessException;
import com.wdd.shakeeat.common.util.JsonArrayUtils;
import com.wdd.shakeeat.module.food.entity.FoodItem;
import com.wdd.shakeeat.module.food.mapper.FoodItemMapper;
import com.wdd.shakeeat.module.history.entity.ShakeHistory;
import com.wdd.shakeeat.module.history.enums.ResultStatus;
import com.wdd.shakeeat.module.history.mapper.ShakeHistoryMapper;
import com.wdd.shakeeat.module.preference.entity.UserPreference;
import com.wdd.shakeeat.module.preference.mapper.UserPreferenceMapper;
import com.wdd.shakeeat.module.shake.dto.ShakeQuotaResponse;
import com.wdd.shakeeat.module.shake.dto.ShakeRequest;
import com.wdd.shakeeat.module.shake.dto.ShakeResponse;
import com.wdd.shakeeat.module.shake.enums.ShakeMode;
import com.wdd.shakeeat.module.shake.enums.TriggerType;
import com.wdd.shakeeat.module.shake.service.impl.ShakeServiceImpl;
import com.wdd.shakeeat.module.tag.entity.FoodItemTagRel;
import com.wdd.shakeeat.module.tag.entity.FoodTag;
import com.wdd.shakeeat.module.tag.mapper.FoodItemTagRelMapper;
import com.wdd.shakeeat.module.tag.mapper.FoodTagMapper;
import com.wdd.shakeeat.support.ai.AiTipService;
import com.wdd.shakeeat.support.order.OrderLinkResolver;
import com.wdd.shakeeat.support.template.FallbackTipBuilder;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShakeServiceImplTest {

    @Mock
    private FoodItemMapper foodItemMapper;

    @Mock
    private FoodItemTagRelMapper foodItemTagRelMapper;

    @Mock
    private UserPreferenceMapper userPreferenceMapper;

    @Mock
    private ShakeHistoryMapper shakeHistoryMapper;

    @Mock
    private FoodTagMapper foodTagMapper;

    @Mock
    private JsonArrayUtils jsonArrayUtils;

    @Mock
    private AiTipService aiTipService;

    @Spy
    private OrderLinkResolver orderLinkResolver;

    @Mock
    private FallbackTipBuilder fallbackTipBuilder;

    @InjectMocks
    private ShakeServiceImpl shakeService;

    @BeforeEach
    void setUp() {
        lenient().when(shakeHistoryMapper.selectCount(any())).thenReturn(0L);
        lenient().when(shakeHistoryMapper.selectList(any())).thenReturn(List.of());
        lenient().when(jsonArrayUtils.parseLongList(eq("[]"))).thenReturn(List.of());
        lenient().when(jsonArrayUtils.toStringJson(any())).thenReturn("[]");
        lenient().when(jsonArrayUtils.toJsonObject(any(), any())).thenAnswer(invocation ->
            new ObjectMapper().writeValueAsString(invocation.getArgument(0))
        );
    }

    @Test
    void shakeShouldRejectWhenDailyCountReached() {
        when(shakeHistoryMapper.selectCount(any())).thenReturn(30L);

        assertThrows(BusinessException.class, () -> shakeService.shake(buildRequest()));
    }

    @Test
    void getQuotaShouldReturnUsedAndRemainingCount() {
        when(shakeHistoryMapper.selectCount(any())).thenReturn(7L);

        ShakeQuotaResponse response = shakeService.getQuota();

        assertEquals(30, response.getDailyLimit());
        assertEquals(7L, response.getUsedCount());
        assertEquals(23L, response.getRemainingCount());
    }

    @Test
    void shakeShouldReturnFallbackResult() {
        FoodItem food = buildFood(1L, "黄焖鸡米饭", 100);
        food.setSteps("1. 打开外卖 App 2. 下单");

        when(userPreferenceMapper.selectOne(any())).thenReturn(buildPreference());
        when(foodItemMapper.selectList(any())).thenReturn(List.of(food));
        when(foodItemTagRelMapper.selectList(any())).thenReturn(List.of(buildRelation(1L, 1L)));
        when(foodTagMapper.selectList(any())).thenReturn(List.of(buildTag(1L, "外卖")));
        when(aiTipService.generateTip(eq(food), eq(ShakeMode.LAZY), any(), any())).thenThrow(new RuntimeException("timeout"));
        when(fallbackTipBuilder.build(food, ShakeMode.LAZY)).thenReturn("今晚吃黄焖鸡米饭, 下单快, 决策成本低, 别再纠结了, 现在执行最省脑子。");
        when(shakeHistoryMapper.insert(any(ShakeHistory.class))).thenAnswer(invocation -> {
            ShakeHistory history = invocation.getArgument(0);
            history.setId(101L);
            return 1;
        });

        ShakeResponse response = shakeService.shake(buildRequest());

        assertEquals(101L, response.getHistoryId());
        assertEquals("黄焖鸡米饭", response.getFoodName());
        assertNotNull(response.getAiTip());
        assertEquals("FALLBACK", response.getAiSource());
        assertEquals(new BigDecimal("180.00"), response.getScoreDetail().getFinalScore());
        verify(shakeHistoryMapper).insert(any(ShakeHistory.class));
    }

    @Test
    void acceptShouldUpdatePendingHistory() {
        ShakeHistory history = new ShakeHistory();
        history.setId(8L);
        history.setResultStatus(ResultStatus.PENDING.name());
        when(shakeHistoryMapper.selectById(8L)).thenReturn(history);

        shakeService.accept(8L);

        ArgumentCaptor<ShakeHistory> captor = ArgumentCaptor.forClass(ShakeHistory.class);
        verify(shakeHistoryMapper).updateById(captor.capture());
        assertEquals(ResultStatus.ACCEPTED.name(), captor.getValue().getResultStatus());
        assertNotNull(captor.getValue().getConfirmTime());
    }

    @Test
    void rejectShouldUpdatePendingHistoryWithFeedback() {
        ShakeHistory history = new ShakeHistory();
        history.setId(9L);
        history.setResultStatus(ResultStatus.PENDING.name());
        when(shakeHistoryMapper.selectById(9L)).thenReturn(history);

        shakeService.reject(9L, "TOO_EXPENSIVE");

        ArgumentCaptor<ShakeHistory> captor = ArgumentCaptor.forClass(ShakeHistory.class);
        verify(shakeHistoryMapper).updateById(captor.capture());
        assertEquals(ResultStatus.REJECTED.name(), captor.getValue().getResultStatus());
        assertEquals("TOO_EXPENSIVE", captor.getValue().getRejectFeedbackCode());
    }

    @Test
    void shakeShouldUseOllamaWhenAiSucceeds() {
        FoodItem food = buildFood(6L, "麻辣香锅外卖", 110);

        when(userPreferenceMapper.selectOne(any())).thenReturn(buildPreference());
        when(foodItemMapper.selectList(any())).thenReturn(List.of(food));
        when(foodItemTagRelMapper.selectList(any())).thenReturn(List.of(buildRelation(6L, 1L)));
        when(foodTagMapper.selectList(any())).thenReturn(List.of(buildTag(1L, "外卖")));
        when(aiTipService.generateTip(eq(food), eq(ShakeMode.LAZY), any(), any())).thenReturn("今天这份麻辣香锅很适合下班后的你, 香辣直接, 不用再纠结。");
        when(shakeHistoryMapper.insert(any(ShakeHistory.class))).thenAnswer(invocation -> {
            ShakeHistory history = invocation.getArgument(0);
            history.setId(202L);
            return 1;
        });

        ShakeResponse response = shakeService.shake(buildRequest());

        assertEquals(202L, response.getHistoryId());
        assertEquals("OLLAMA", response.getAiSource());
        assertEquals(new BigDecimal("198.00"), response.getScoreDetail().getFinalScore());
    }

    @Test
    void shakeShouldBroadenCandidatesWhenModeHitsTooFewAndSkipLatestFood() {
        FoodItem latestModeFood = buildFood(1L, "黄焖鸡米饭", 120);
        FoodItem backupFood = buildFood(2L, "番茄鸡蛋面", 95);

        when(userPreferenceMapper.selectOne(any())).thenReturn(buildPreference());
        when(foodItemMapper.selectList(any())).thenReturn(List.of(latestModeFood, backupFood));
        when(foodItemTagRelMapper.selectList(any())).thenReturn(List.of(
            buildRelation(1L, 1L),
            buildRelation(2L, 8L)
        ));
        when(foodTagMapper.selectList(any())).thenReturn(List.of(
            buildTag(1L, "外卖"),
            buildTag(8L, "面食")
        ));
        when(shakeHistoryMapper.selectList(any())).thenReturn(List.of(buildHistory(99L, 1L, "黄焖鸡米饭")));
        when(aiTipService.generateTip(eq(backupFood), eq(ShakeMode.LAZY), any(), any())).thenReturn("今晚换个口味, 这碗面更稳。");
        when(shakeHistoryMapper.insert(any(ShakeHistory.class))).thenAnswer(invocation -> {
            ShakeHistory history = invocation.getArgument(0);
            history.setId(303L);
            return 1;
        });

        ShakeResponse response = shakeService.shake(buildRequest());

        assertEquals(303L, response.getHistoryId());
        assertEquals("番茄鸡蛋面", response.getFoodName());
        assertEquals(new BigDecimal("95.00"), response.getScoreDetail().getFinalScore());
    }

    @Test
    void shakeShouldApplyStrongDiversityPenaltyToScoreSnapshot() {
        FoodItem food = buildFood(1L, "黄焖鸡米饭", 100);

        when(userPreferenceMapper.selectOne(any())).thenReturn(buildPreference());
        when(foodItemMapper.selectList(any())).thenReturn(List.of(food));
        when(foodItemTagRelMapper.selectList(any())).thenReturn(List.of(buildRelation(1L, 1L)));
        when(foodTagMapper.selectList(any())).thenReturn(List.of(buildTag(1L, "外卖")));
        when(shakeHistoryMapper.selectList(any())).thenReturn(List.of(buildHistory(88L, 1L, "黄焖鸡米饭")));
        when(aiTipService.generateTip(eq(food), eq(ShakeMode.LAZY), any(), any())).thenReturn("还是它, 但分数会被压低。");
        when(shakeHistoryMapper.insert(any(ShakeHistory.class))).thenAnswer(invocation -> {
            ShakeHistory history = invocation.getArgument(0);
            history.setId(404L);
            return 1;
        });

        ShakeResponse response = shakeService.shake(buildRequest());

        ArgumentCaptor<ShakeHistory> captor = ArgumentCaptor.forClass(ShakeHistory.class);
        verify(shakeHistoryMapper).insert(captor.capture());
        assertEquals(new BigDecimal("63.00"), response.getScoreDetail().getFinalScore());
        assertNotNull(captor.getValue().getScoreSnapshot());
    }

    @Test
    void shakeShouldApplyWeakDiversityPenaltyToScoreSnapshot() {
        FoodItem food = buildFood(1L, "黄焖鸡米饭", 100);

        when(userPreferenceMapper.selectOne(any())).thenReturn(buildPreference());
        when(foodItemMapper.selectList(any())).thenReturn(List.of(food));
        when(foodItemTagRelMapper.selectList(any())).thenReturn(List.of(buildRelation(1L, 1L)));
        when(foodTagMapper.selectList(any())).thenReturn(List.of(buildTag(1L, "外卖")));
        when(shakeHistoryMapper.selectList(any())).thenReturn(List.of(
            buildHistory(91L, 2L, "麻辣香锅外卖"),
            buildHistory(90L, 3L, "番茄鸡蛋面"),
            buildHistory(89L, 4L, "轻食鸡胸沙拉"),
            buildHistory(88L, 1L, "黄焖鸡米饭")
        ));
        when(aiTipService.generateTip(eq(food), eq(ShakeMode.LAZY), any(), any())).thenReturn("最近出现过, 但还没被强压。");
        when(shakeHistoryMapper.insert(any(ShakeHistory.class))).thenAnswer(invocation -> {
            ShakeHistory history = invocation.getArgument(0);
            history.setId(505L);
            return 1;
        });

        ShakeResponse response = shakeService.shake(buildRequest());

        ArgumentCaptor<ShakeHistory> captor = ArgumentCaptor.forClass(ShakeHistory.class);
        verify(shakeHistoryMapper).insert(captor.capture());
        assertEquals(new BigDecimal("117.00"), response.getScoreDetail().getFinalScore());
        assertNotNull(captor.getValue().getScoreSnapshot());
    }

    @Test
    void shakeShouldReturnNormalizedSearchUrlAndSteps() {
        FoodItem food = buildFood(8L, "炸鸡汉堡外卖", 105);
        food.setSteps("1. 打开外卖 App 2. 选炸鸡汉堡套餐 3. 下单");
        food.setReferencePrice(new BigDecimal("29.00"));
        food.setOrderUrl("https://waimai.meituan.com/?keyword=%E7%82%B8%E9%B8%A1%E6%B1%89%E5%A0%A1");

        when(userPreferenceMapper.selectOne(any())).thenReturn(buildPreference());
        when(foodItemMapper.selectList(any())).thenReturn(List.of(food));
        when(foodItemTagRelMapper.selectList(any())).thenReturn(List.of(buildRelation(8L, 1L), buildRelation(8L, 9L)));
        when(foodTagMapper.selectList(any())).thenReturn(List.of(buildTag(1L, "外卖"), buildTag(9L, "夜宵")));
        when(jsonArrayUtils.toStringJson(eq(List.of("外卖", "夜宵")))).thenReturn("[\"外卖\",\"夜宵\"]");
        when(aiTipService.generateTip(eq(food), eq(ShakeMode.LAZY), any(), any())).thenReturn("夜深了, 这份炸鸡汉堡更适合快速收尾。");
        when(shakeHistoryMapper.insert(any(ShakeHistory.class))).thenAnswer(invocation -> {
            ShakeHistory history = invocation.getArgument(0);
            history.setId(606L);
            return 1;
        });

        ShakeResponse response = shakeService.shake(buildRequest());

        assertEquals(
            "https://waimai.meituan.com/?keyword=%E7%82%B8%E9%B8%A1%E6%B1%89%E5%A0%A1%E5%A4%96%E5%8D%96%2029%E5%85%83",
            response.getOrderUrl()
        );
        assertEquals("1. 点击下方美团搜索入口 2. 搜索\"炸鸡汉堡外卖 29元\" 3. 对比店铺价格后下单", response.getSteps());
        assertEquals(new BigDecimal("189.00"), response.getScoreDetail().getFinalScore());
        assertEquals(4, response.getScoreDetail().getReasonLabels().size());
    }

    private ShakeRequest buildRequest() {
        ShakeRequest request = new ShakeRequest();
        request.setMode(ShakeMode.LAZY);
        request.setTriggerType(TriggerType.CLICK);
        return request;
    }

    private UserPreference buildPreference() {
        UserPreference preference = new UserPreference();
        preference.setUserId(1L);
        preference.setFavoriteTagIds("[]");
        preference.setAvoidTagIds("[]");
        return preference;
    }

    private FoodItem buildFood(Long id, String name, int weight) {
        FoodItem food = new FoodItem();
        food.setId(id);
        food.setName(name);
        food.setWeight(weight);
        food.setDeleted(0);
        food.setEnabled(1);
        return food;
    }

    private FoodItemTagRel buildRelation(Long foodId, Long tagId) {
        FoodItemTagRel relation = new FoodItemTagRel();
        relation.setFoodId(foodId);
        relation.setTagId(tagId);
        return relation;
    }

    private FoodTag buildTag(Long id, String name) {
        FoodTag tag = new FoodTag();
        tag.setId(id);
        tag.setName(name);
        return tag;
    }

    private ShakeHistory buildHistory(Long id, Long foodId, String foodName) {
        ShakeHistory history = new ShakeHistory();
        history.setId(id);
        history.setFoodId(foodId);
        history.setFoodName(foodName);
        return history;
    }
}
