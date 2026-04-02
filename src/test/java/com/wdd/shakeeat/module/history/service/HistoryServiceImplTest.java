package com.wdd.shakeeat.module.history.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wdd.shakeeat.common.util.JsonArrayUtils;
import com.wdd.shakeeat.module.food.entity.FoodItem;
import com.wdd.shakeeat.module.food.mapper.FoodItemMapper;
import com.wdd.shakeeat.module.history.dto.HistoryDetailResponse;
import com.wdd.shakeeat.module.history.dto.HistoryPageResponse;
import com.wdd.shakeeat.module.history.dto.HistoryStatsResponse;
import com.wdd.shakeeat.module.history.entity.ShakeHistory;
import com.wdd.shakeeat.module.history.mapper.ShakeHistoryMapper;
import com.wdd.shakeeat.module.history.service.impl.HistoryServiceImpl;
import com.wdd.shakeeat.module.shake.dto.ScoreDetailResponse;
import com.wdd.shakeeat.support.order.OrderLinkResolver;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HistoryServiceImplTest {

    @Mock
    private ShakeHistoryMapper shakeHistoryMapper;

    @Mock
    private FoodItemMapper foodItemMapper;

    @Spy
    private OrderLinkResolver orderLinkResolver;

    @Mock
    private JsonArrayUtils jsonArrayUtils;

    @InjectMocks
    private HistoryServiceImpl historyService;

    @Test
    void listHistoriesShouldBuildPreview() {
        ShakeHistory history = new ShakeHistory();
        history.setId(1L);
        history.setFoodName("黄焖鸡米饭");
        history.setMode("LAZY");
        history.setResultStatus("PENDING");
        history.setAiTip("今晚吃黄焖鸡米饭, 下单快, 决策成本低, 别再纠结了, 现在执行最省脑子。");

        Page<ShakeHistory> page = new Page<>(1, 30);
        page.setRecords(List.of(history));
        page.setTotal(1L);
        when(shakeHistoryMapper.selectPage(org.mockito.ArgumentMatchers.<Page<ShakeHistory>>any(), any())).thenReturn(page);

        HistoryPageResponse response = historyService.listHistories(null, 1, 30);

        assertEquals(1L, response.getTotal());
        assertEquals("黄焖鸡米饭", response.getRecords().get(0).getFoodName());
        assertNotNull(response.getRecords().get(0).getAiTipPreview());
    }

    @Test
    void getStatsShouldAggregateDistribution() {
        ShakeHistory accepted = new ShakeHistory();
        accepted.setId(1L);
        accepted.setMode("LAZY");
        accepted.setResultStatus("ACCEPTED");
        accepted.setShakeTime(LocalDateTime.now().minusDays(1));

        ShakeHistory rejected = new ShakeHistory();
        rejected.setId(2L);
        rejected.setMode("SPICY");
        rejected.setResultStatus("REJECTED");
        rejected.setRejectFeedbackCode("TOO_EXPENSIVE");
        rejected.setShakeTime(LocalDateTime.now());

        ShakeHistory pending = new ShakeHistory();
        pending.setId(3L);
        pending.setMode("LAZY");
        pending.setResultStatus("PENDING");
        pending.setShakeTime(LocalDateTime.now().minusDays(2));

        when(shakeHistoryMapper.selectList(any())).thenReturn(List.of(accepted, rejected, pending));

        HistoryStatsResponse response = historyService.getStats();

        assertEquals(3L, response.getTotalCount());
        assertEquals(1L, response.getAcceptedCount());
        assertEquals(1L, response.getRejectedCount());
        assertEquals(1L, response.getPendingCount());
        assertEquals(new BigDecimal("33.33"), response.getAcceptanceRate());
        assertEquals("LAZY", response.getModeStats().get(0).getCode());
        assertEquals(2L, response.getModeStats().get(0).getCount());
        assertEquals("TOO_EXPENSIVE", response.getRejectFeedbackStats().get(0).getCode());
        assertEquals("太贵", response.getRejectFeedbackStats().get(0).getLabel());
        assertEquals(7, response.getRecentDailyStats().size());
    }

    @Test
    void getHistoryShouldReturnNormalizedStepsAndOrderUrl() {
        ShakeHistory history = new ShakeHistory();
        history.setId(9L);
        history.setFoodId(3L);
        history.setFoodName("番茄鸡蛋面");
        history.setAiTip("今天来一碗热乎的面。");
        history.setTagSnapshot("[\"外卖\",\"夜宵\"]");
        history.setScoreSnapshot("{\"finalScore\":189.00}");
        history.setRejectFeedbackCode("TOO_EXPENSIVE");
        when(shakeHistoryMapper.selectById(9L)).thenReturn(history);

        FoodItem food = new FoodItem();
        food.setId(3L);
        food.setName("炸鸡汉堡外卖");
        food.setSteps("1. 打开外卖 App 2. 选炸鸡汉堡套餐 3. 下单");
        food.setReferencePrice(new BigDecimal("29.00"));
        food.setOrderUrl("https://waimai.meituan.com/?keyword=%E7%82%B8%E9%B8%A1%E6%B1%89%E5%A0%A1");
        when(foodItemMapper.selectById(3L)).thenReturn(food);
        when(jsonArrayUtils.parseStringList(eq("[\"外卖\",\"夜宵\"]"))).thenReturn(List.of("外卖", "夜宵"));
        when(jsonArrayUtils.parseObject(eq("{\"finalScore\":189.00}"), eq(ScoreDetailResponse.class), any()))
            .thenReturn(ScoreDetailResponse.builder().finalScore(new BigDecimal("189.00")).build());

        HistoryDetailResponse response = historyService.getHistory(9L);

        assertEquals("番茄鸡蛋面", response.getFoodName());
        assertEquals("1. 点击下方美团搜索入口 2. 搜索\"炸鸡汉堡外卖 29元\" 3. 对比店铺价格后下单", response.getSteps());
        assertEquals(new BigDecimal("29.00"), response.getReferencePrice());
        assertEquals(List.of("外卖", "夜宵"), response.getTagNames());
        assertEquals(new BigDecimal("189.00"), response.getScoreDetail().getFinalScore());
        assertEquals("TOO_EXPENSIVE", response.getRejectFeedbackCode());
        assertEquals("太贵", response.getRejectFeedbackLabel());
        assertEquals(
            "https://waimai.meituan.com/?keyword=%E7%82%B8%E9%B8%A1%E6%B1%89%E5%A0%A1%E5%A4%96%E5%8D%96%2029%E5%85%83",
            response.getOrderUrl()
        );
    }

    @Test
    void getHistoryShouldPreferSnapshotWhenPresent() {
        ShakeHistory history = new ShakeHistory();
        history.setId(10L);
        history.setFoodId(4L);
        history.setFoodName("轻食鸡胸沙拉");
        history.setStepsSnapshot("1. 打开冷藏柜 2. 拿出沙拉 3. 直接吃");
        history.setOrderUrlSnapshot("https://example.com/salad");
        history.setReferencePriceSnapshot(new BigDecimal("18.00"));
        history.setTagSnapshot("[\"轻食\"]");
        history.setScoreSnapshot("{\"finalScore\":80.00}");
        when(shakeHistoryMapper.selectById(10L)).thenReturn(history);
        when(foodItemMapper.selectById(4L)).thenReturn(null);
        when(jsonArrayUtils.parseStringList(eq("[\"轻食\"]"))).thenReturn(List.of("轻食"));
        when(jsonArrayUtils.parseObject(eq("{\"finalScore\":80.00}"), eq(ScoreDetailResponse.class), any()))
            .thenReturn(ScoreDetailResponse.builder().finalScore(new BigDecimal("80.00")).build());

        HistoryDetailResponse response = historyService.getHistory(10L);

        assertEquals("1. 打开冷藏柜 2. 拿出沙拉 3. 直接吃", response.getSteps());
        assertEquals("https://example.com/salad", response.getOrderUrl());
        assertEquals(new BigDecimal("18.00"), response.getReferencePrice());
        assertEquals(List.of("轻食"), response.getTagNames());
        assertEquals(new BigDecimal("80.00"), response.getScoreDetail().getFinalScore());
    }
}
