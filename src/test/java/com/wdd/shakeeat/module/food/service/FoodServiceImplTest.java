package com.wdd.shakeeat.module.food.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wdd.shakeeat.common.exception.BusinessException;
import com.wdd.shakeeat.module.food.dto.FoodExportItemResponse;
import com.wdd.shakeeat.module.food.dto.FoodImportItemRequest;
import com.wdd.shakeeat.module.food.dto.FoodImportResponse;
import com.wdd.shakeeat.module.food.dto.FoodSaveRequest;
import com.wdd.shakeeat.module.food.entity.FoodItem;
import com.wdd.shakeeat.module.food.mapper.FoodItemMapper;
import com.wdd.shakeeat.module.food.service.impl.FoodServiceImpl;
import com.wdd.shakeeat.module.tag.entity.FoodItemTagRel;
import com.wdd.shakeeat.module.tag.mapper.FoodItemTagRelMapper;
import com.wdd.shakeeat.support.order.OrderLinkResolver;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FoodServiceImplTest {

    @Mock
    private FoodItemMapper foodItemMapper;

    @Mock
    private FoodItemTagRelMapper foodItemTagRelMapper;

    @Spy
    private OrderLinkResolver orderLinkResolver;

    @InjectMocks
    private FoodServiceImpl foodService;

    @Test
    void createFoodShouldRejectDuplicateName() {
        FoodSaveRequest request = buildRequest();
        when(foodItemMapper.selectCount(any())).thenReturn(1L);

        assertThrows(BusinessException.class, () -> foodService.createFood(request));
        verify(foodItemMapper, never()).insert(any(FoodItem.class));
    }

    @Test
    void updateEnabledShouldPersistFlag() {
        FoodItem food = new FoodItem();
        food.setId(1L);
        food.setDeleted(0);
        food.setEnabled(1);
        when(foodItemMapper.selectById(1L)).thenReturn(food);

        foodService.updateEnabled(1L, false);

        ArgumentCaptor<FoodItem> captor = ArgumentCaptor.forClass(FoodItem.class);
        verify(foodItemMapper).updateById(captor.capture());
        assertEquals(0, captor.getValue().getEnabled());
    }

    @Test
    void deleteFoodShouldMarkDeletedAndClearRelations() {
        FoodItem food = new FoodItem();
        food.setId(9L);
        food.setDeleted(0);
        when(foodItemMapper.selectById(9L)).thenReturn(food);

        foodService.deleteFood(9L);

        verify(foodItemMapper).updateById(any(FoodItem.class));
        verify(foodItemTagRelMapper).delete(any());
    }

    @Test
    void createFoodShouldSaveRelations() {
        FoodSaveRequest request = buildRequest();
        when(foodItemMapper.selectCount(any())).thenReturn(0L);
        when(foodItemTagRelMapper.selectList(any())).thenReturn(List.of());
        when(foodItemMapper.selectById(eq(1L))).thenAnswer(invocation -> {
            FoodItem food = new FoodItem();
            food.setId(1L);
            food.setDeleted(0);
            food.setEnabled(1);
            food.setName("黄焖鸡米饭");
            food.setOrderUrl("https://waimai.meituan.com/?keyword=%E9%BB%84%E7%84%96%E9%B8%A1%E7%B1%B3%E9%A5%AD");
            food.setReferencePrice(new BigDecimal("22.00"));
            food.setWeight(100);
            return food;
        });
        when(foodItemMapper.insert(any(FoodItem.class))).thenAnswer(invocation -> {
            FoodItem food = invocation.getArgument(0);
            food.setId(1L);
            return 1;
        });

        foodService.createFood(request);

        ArgumentCaptor<FoodItem> foodCaptor = ArgumentCaptor.forClass(FoodItem.class);
        verify(foodItemMapper).insert(foodCaptor.capture());
        assertEquals(
            "https://waimai.meituan.com/?keyword=%E9%BB%84%E7%84%96%E9%B8%A1%E7%B1%B3%E9%A5%AD",
            foodCaptor.getValue().getOrderUrl()
        );
        verify(foodItemTagRelMapper, times(2)).insert(any(FoodItemTagRel.class));
    }

    @Test
    void createFoodShouldNormalizeMeituanSearchUrlByFullNameAndPrice() {
        FoodSaveRequest request = buildRequest();
        request.setName("炸鸡汉堡外卖");
        request.setSteps("1. 打开外卖 App 2. 选炸鸡汉堡套餐 3. 下单");
        request.setOrderUrl("https://waimai.meituan.com/?keyword=%E7%82%B8%E9%B8%A1%E6%B1%89%E5%A0%A1");
        request.setReferencePrice(new BigDecimal("29.00"));
        when(foodItemMapper.selectCount(any())).thenReturn(0L);
        when(foodItemTagRelMapper.selectList(any())).thenReturn(List.of());
        when(foodItemMapper.selectById(eq(1L))).thenAnswer(invocation -> {
            FoodItem food = new FoodItem();
            food.setId(1L);
            food.setDeleted(0);
            food.setEnabled(1);
            food.setName("炸鸡汉堡外卖");
            food.setOrderUrl("https://waimai.meituan.com/?keyword=%E7%82%B8%E9%B8%A1%E6%B1%89%E5%A0%A1%E5%A4%96%E5%8D%96%2029%E5%85%83");
            food.setReferencePrice(new BigDecimal("29.00"));
            food.setWeight(100);
            return food;
        });
        when(foodItemMapper.insert(any(FoodItem.class))).thenAnswer(invocation -> {
            FoodItem food = invocation.getArgument(0);
            food.setId(1L);
            return 1;
        });

        foodService.createFood(request);

        ArgumentCaptor<FoodItem> foodCaptor = ArgumentCaptor.forClass(FoodItem.class);
        verify(foodItemMapper).insert(foodCaptor.capture());
        assertEquals(
            "https://waimai.meituan.com/?keyword=%E7%82%B8%E9%B8%A1%E6%B1%89%E5%A0%A1%E5%A4%96%E5%8D%96%2029%E5%85%83",
            foodCaptor.getValue().getOrderUrl()
        );
    }

    @Test
    void exportFoodsShouldReturnFullFields() {
        FoodItem food = new FoodItem();
        food.setId(1L);
        food.setName("寿司");
        food.setEmoji("🍣");
        food.setSteps("1. 下单 2. 取餐");
        food.setImageUrl("https://img.example.com/sushi.png");
        food.setOrderUrl("https://example.com/order/sushi");
        food.setWeight(88);
        food.setReferencePrice(new BigDecimal("36.00"));
        food.setEnabled(1);
        food.setDeleted(0);
        when(foodItemMapper.selectList(any())).thenReturn(List.of(food));
        when(foodItemTagRelMapper.selectList(any())).thenReturn(List.of(buildRelation(1L, 3L), buildRelation(1L, 5L)));

        List<FoodExportItemResponse> result = foodService.exportFoods();

        assertEquals(1, result.size());
        assertEquals("寿司", result.get(0).getName());
        assertEquals("🍣", result.get(0).getEmoji());
        assertEquals("1. 下单 2. 取餐", result.get(0).getSteps());
        assertEquals("https://img.example.com/sushi.png", result.get(0).getImageUrl());
        assertEquals("https://example.com/order/sushi", result.get(0).getOrderUrl());
        assertEquals(88, result.get(0).getWeight());
        assertEquals(new BigDecimal("36.00"), result.get(0).getReferencePrice());
        assertEquals(1, result.get(0).getEnabled());
        assertEquals(List.of(3L, 5L), result.get(0).getTagIds());
    }

    @Test
    void importFoodsShouldCreateNewFood() {
        FoodImportItemRequest item = buildImportItem("寿司卷", 66, 0, List.of(3L, 4L));
        when(foodItemTagRelMapper.selectList(any())).thenReturn(List.of());
        when(foodItemMapper.selectOne(any())).thenReturn(null);
        when(foodItemMapper.insert(any(FoodItem.class))).thenAnswer(invocation -> {
            FoodItem food = invocation.getArgument(0);
            food.setId(5L);
            return 1;
        });

        FoodImportResponse result = foodService.importFoods(List.of(item));

        ArgumentCaptor<FoodItem> captor = ArgumentCaptor.forClass(FoodItem.class);
        verify(foodItemMapper).insert(captor.capture());
        assertEquals("寿司卷", captor.getValue().getName());
        assertEquals(66, captor.getValue().getWeight());
        assertEquals(0, captor.getValue().getEnabled());
        verify(foodItemTagRelMapper).delete(any());
        verify(foodItemTagRelMapper, times(2)).insert(any(FoodItemTagRel.class));
        assertEquals(1, result.getCreatedCount());
        assertEquals(0, result.getUpdatedCount());
        assertEquals(0, result.getSkippedCount());
    }

    @Test
    void importFoodsShouldUpdateExistingFoodByName() {
        FoodItem current = new FoodItem();
        current.setId(7L);
        current.setName("盖浇饭");
        current.setEmoji("🍛");
        current.setSteps("1. 搜索 2. 下单");
        current.setImageUrl("https://img.example.com/food.png");
        current.setOrderUrl("https://waimai.meituan.com/?keyword=%E7%9B%96%E6%B5%87%E9%A5%AD%2024%E5%85%83");
        current.setWeight(80);
        current.setReferencePrice(new BigDecimal("24.00"));
        current.setEnabled(1);
        current.setDeleted(0);
        FoodImportItemRequest item = buildImportItem("盖浇饭", 120, 0, List.of(1L, 2L));
        when(foodItemTagRelMapper.selectList(any())).thenReturn(List.of(buildRelation(7L, 1L)));
        when(foodItemMapper.selectOne(any())).thenReturn(current);

        FoodImportResponse result = foodService.importFoods(List.of(item));

        ArgumentCaptor<FoodItem> captor = ArgumentCaptor.forClass(FoodItem.class);
        verify(foodItemMapper).updateById(captor.capture());
        assertEquals(120, captor.getValue().getWeight());
        assertEquals(0, captor.getValue().getEnabled());
        verify(foodItemTagRelMapper).delete(any());
        verify(foodItemTagRelMapper, times(2)).insert(any(FoodItemTagRel.class));
        assertEquals(0, result.getCreatedCount());
        assertEquals(1, result.getUpdatedCount());
        assertEquals(0, result.getSkippedCount());
    }

    @Test
    void importFoodsShouldCountCreatedUpdatedAndSkipped() {
        FoodItem updateFood = new FoodItem();
        updateFood.setId(2L);
        updateFood.setName("盖浇饭");
        updateFood.setEmoji("🍛");
        updateFood.setSteps("1. 搜索 2. 下单");
        updateFood.setImageUrl("https://img.example.com/food.png");
        updateFood.setOrderUrl("https://waimai.meituan.com/?keyword=%E7%9B%96%E6%B5%87%E9%A5%AD%2024%E5%85%83");
        updateFood.setWeight(80);
        updateFood.setReferencePrice(new BigDecimal("24.00"));
        updateFood.setEnabled(1);
        updateFood.setDeleted(0);

        FoodItem sameFood = new FoodItem();
        sameFood.setId(3L);
        sameFood.setName("馄饨");
        sameFood.setEmoji("🍱");
        sameFood.setSteps("1. 搜索 2. 下单");
        sameFood.setImageUrl("https://img.example.com/food.png");
        sameFood.setOrderUrl("https://waimai.meituan.com/?keyword=%E9%A6%84%E9%A5%A8%2020%E5%85%83");
        sameFood.setWeight(70);
        sameFood.setReferencePrice(new BigDecimal("20.00"));
        sameFood.setEnabled(1);
        sameFood.setDeleted(0);
        when(foodItemTagRelMapper.selectList(any())).thenReturn(List.of(buildRelation(2L, 1L), buildRelation(3L, 9L)));
        when(foodItemMapper.selectOne(any())).thenReturn(null, updateFood, sameFood);
        when(foodItemMapper.insert(any(FoodItem.class))).thenAnswer(invocation -> {
            FoodItem food = invocation.getArgument(0);
            food.setId(1L);
            return 1;
        });

        FoodImportResponse result = foodService.importFoods(List.of(
            buildImportItem("酸辣粉", 55, 1, List.of(6L)),
            buildImportItem("盖浇饭", 99, 1, List.of(1L, 2L), new BigDecimal("24.00")),
            buildImportItem("馄饨", 70, 1, List.of(9L))
        ));

        assertEquals(1, result.getCreatedCount());
        assertEquals(1, result.getUpdatedCount());
        assertEquals(1, result.getSkippedCount());
        verify(foodItemMapper, times(1)).insert(any(FoodItem.class));
        verify(foodItemMapper, times(1)).updateById(any(FoodItem.class));
    }

    private FoodSaveRequest buildRequest() {
        FoodSaveRequest request = new FoodSaveRequest();
        request.setName("黄焖鸡米饭");
        request.setOrderUrl("https://waimai.meituan.com/?keyword=%E9%BB%84%E7%84%96%E9%B8%A1%E7%B1%B3%E9%A5%AD");
        request.setWeight(100);
        request.setTagIds(List.of(1L, 2L));
        return request;
    }

    private FoodImportItemRequest buildImportItem(String name, int weight, int enabled, List<Long> tagIds) {
        return buildImportItem(name, weight, enabled, tagIds, new BigDecimal("20.00"));
    }

    private FoodImportItemRequest buildImportItem(
        String name,
        int weight,
        int enabled,
        List<Long> tagIds,
        BigDecimal referencePrice
    ) {
        FoodImportItemRequest request = new FoodImportItemRequest();
        request.setName(name);
        request.setEmoji("🍱");
        request.setSteps("1. 搜索 2. 下单");
        request.setImageUrl("https://img.example.com/food.png");
        request.setOrderUrl("https://waimai.meituan.com/?keyword=" + name);
        request.setWeight(weight);
        request.setReferencePrice(referencePrice);
        request.setEnabled(enabled);
        request.setTagIds(tagIds);
        return request;
    }

    private FoodItemTagRel buildRelation(Long foodId, Long tagId) {
        FoodItemTagRel relation = new FoodItemTagRel();
        relation.setFoodId(foodId);
        relation.setTagId(tagId);
        return relation;
    }
}
