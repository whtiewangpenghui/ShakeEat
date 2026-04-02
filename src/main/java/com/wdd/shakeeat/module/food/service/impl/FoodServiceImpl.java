package com.wdd.shakeeat.module.food.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wdd.shakeeat.common.exception.BusinessException;
import com.wdd.shakeeat.module.food.dto.FoodDetailResponse;
import com.wdd.shakeeat.module.food.dto.FoodExportItemResponse;
import com.wdd.shakeeat.module.food.dto.FoodImportItemRequest;
import com.wdd.shakeeat.module.food.dto.FoodImportResponse;
import com.wdd.shakeeat.module.food.dto.FoodListResponse;
import com.wdd.shakeeat.module.food.dto.FoodSaveRequest;
import com.wdd.shakeeat.module.food.entity.FoodItem;
import com.wdd.shakeeat.module.food.mapper.FoodItemMapper;
import com.wdd.shakeeat.module.food.service.FoodService;
import com.wdd.shakeeat.module.tag.entity.FoodItemTagRel;
import com.wdd.shakeeat.module.tag.mapper.FoodItemTagRelMapper;
import com.wdd.shakeeat.support.order.OrderLinkResolver;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodItemMapper foodItemMapper;
    private final FoodItemTagRelMapper foodItemTagRelMapper;
    private final OrderLinkResolver orderLinkResolver;

    @Override
    public List<FoodListResponse> listFoods() {
        List<FoodItem> foods = foodItemMapper.selectList(buildQuery());
        Map<Long, List<Long>> tagMap = buildTagMap();
        return foods.stream().map(food -> toResponse(food, tagMap)).toList();
    }

    @Override
    public List<FoodExportItemResponse> exportFoods() {
        List<FoodItem> foods = foodItemMapper.selectList(buildQuery());
        Map<Long, List<Long>> tagMap = buildTagMap();
        return foods.stream().map(food -> toExport(food, tagMap)).toList();
    }

    @Override
    public FoodDetailResponse getFood(Long id) {
        FoodItem food = getActiveFood(id);
        return toDetail(food, buildTagMap());
    }

    @Override
    @Transactional
    public FoodDetailResponse createFood(FoodSaveRequest request) {
        ensureNameUnique(request.getName(), null);
        FoodItem food = buildFood(null, request);
        foodItemMapper.insert(food);
        replaceTags(food.getId(), request.getTagIds());
        return getFood(food.getId());
    }

    @Override
    @Transactional
    public FoodDetailResponse updateFood(Long id, FoodSaveRequest request) {
        FoodItem current = getActiveFood(id);
        ensureNameUnique(request.getName(), id);
        FoodItem food = buildFood(current, request);
        foodItemMapper.updateById(food);
        replaceTags(id, request.getTagIds());
        return getFood(id);
    }

    @Override
    @Transactional
    public void deleteFood(Long id) {
        FoodItem food = getActiveFood(id);
        food.setDeleted(1);
        foodItemMapper.updateById(food);
        // 食物下线后立即清理标签关系, 避免后续列表和推荐继续扫到脏数据。
        deleteRelations(id);
    }

    @Override
    public void updateEnabled(Long id, boolean enabled) {
        FoodItem food = getActiveFood(id);
        food.setEnabled(enabled ? 1 : 0);
        foodItemMapper.updateById(food);
    }

    @Override
    @Transactional
    public FoodImportResponse importFoods(List<FoodImportItemRequest> items) {
        int createdCount = 0;
        int updatedCount = 0;
        int skippedCount = 0;
        Map<Long, List<Long>> tagMap = buildTagMap();
        for (FoodImportItemRequest item : items) {
            FoodItem current = findActiveFoodByName(normalizeName(item.getName()));
            ImportAction action = importFood(item, current, tagMap);
            if (action == ImportAction.CREATED) {
                createdCount++;
            } else if (action == ImportAction.UPDATED) {
                updatedCount++;
            } else {
                skippedCount++;
            }
        }
        return FoodImportResponse.builder()
            .createdCount(createdCount)
            .updatedCount(updatedCount)
            .skippedCount(skippedCount)
            .build();
    }

    private LambdaQueryWrapper<FoodItem> buildQuery() {
        LambdaQueryWrapper<FoodItem> query = new LambdaQueryWrapper<>();
        query.eq(FoodItem::getDeleted, 0);
        query.orderByDesc(FoodItem::getUpdateTime, FoodItem::getId);
        return query;
    }

    private Map<Long, List<Long>> buildTagMap() {
        LambdaQueryWrapper<FoodItemTagRel> query = new LambdaQueryWrapper<>();
        return foodItemTagRelMapper.selectList(query).stream()
            .collect(java.util.stream.Collectors.groupingBy(
                FoodItemTagRel::getFoodId,
                java.util.stream.Collectors.mapping(FoodItemTagRel::getTagId, java.util.stream.Collectors.toList())
            ));
    }

    private FoodListResponse toResponse(FoodItem food, Map<Long, List<Long>> tagMap) {
        return FoodListResponse.builder()
            .id(food.getId())
            .name(food.getName())
            .emoji(food.getEmoji())
            .orderUrl(orderLinkResolver.resolveDisplayUrl(food))
            .weight(food.getWeight())
            .referencePrice(food.getReferencePrice())
            .enabled(food.getEnabled())
            .tagIds(tagMap.getOrDefault(food.getId(), Collections.emptyList()))
            .build();
    }

    private FoodExportItemResponse toExport(FoodItem food, Map<Long, List<Long>> tagMap) {
        return FoodExportItemResponse.builder()
            .name(food.getName())
            .emoji(food.getEmoji())
            .steps(food.getSteps())
            .imageUrl(food.getImageUrl())
            .orderUrl(orderLinkResolver.resolveDisplayUrl(food))
            .weight(food.getWeight())
            .referencePrice(food.getReferencePrice())
            .enabled(food.getEnabled())
            .tagIds(tagMap.getOrDefault(food.getId(), Collections.emptyList()))
            .build();
    }

    private FoodDetailResponse toDetail(FoodItem food, Map<Long, List<Long>> tagMap) {
        return FoodDetailResponse.builder()
            .id(food.getId())
            .name(food.getName())
            .emoji(food.getEmoji())
            .steps(orderLinkResolver.resolveDisplaySteps(food))
            .imageUrl(food.getImageUrl())
            .orderUrl(orderLinkResolver.resolveDisplayUrl(food))
            .weight(food.getWeight())
            .referencePrice(food.getReferencePrice())
            .enabled(food.getEnabled())
            .tagIds(tagMap.getOrDefault(food.getId(), Collections.emptyList()))
            .build();
    }

    private FoodItem getActiveFood(Long id) {
        FoodItem food = foodItemMapper.selectById(id);
        if (food == null || Integer.valueOf(1).equals(food.getDeleted())) {
            throw new BusinessException("食物不存在");
        }
        return food;
    }

    private FoodItem findActiveFoodByName(String name) {
        LambdaQueryWrapper<FoodItem> query = new LambdaQueryWrapper<>();
        query.eq(FoodItem::getName, name);
        query.eq(FoodItem::getDeleted, 0);
        return foodItemMapper.selectOne(query);
    }

    private void ensureNameUnique(String name, Long excludeId) {
        LambdaQueryWrapper<FoodItem> query = new LambdaQueryWrapper<>();
        query.eq(FoodItem::getName, name);
        query.eq(FoodItem::getDeleted, 0);
        if (excludeId != null) {
            query.ne(FoodItem::getId, excludeId);
        }
        if (foodItemMapper.selectCount(query) > 0) {
            throw new BusinessException("食物名称已存在");
        }
    }

    private FoodItem buildFood(FoodItem current, FoodSaveRequest request) {
        return buildFood(
            current,
            request.getName(),
            request.getEmoji(),
            request.getSteps(),
            request.getImageUrl(),
            request.getOrderUrl(),
            request.getWeight(),
            request.getReferencePrice(),
            null
        );
    }

    private FoodItem buildFood(FoodItem current, FoodImportItemRequest request) {
        return buildFood(
            current,
            request.getName(),
            request.getEmoji(),
            request.getSteps(),
            request.getImageUrl(),
            request.getOrderUrl(),
            request.getWeight(),
            request.getReferencePrice(),
            request.getEnabled()
        );
    }

    private FoodItem buildFood(
        FoodItem current,
        String name,
        String emoji,
        String steps,
        String imageUrl,
        String orderUrl,
        Integer weight,
        java.math.BigDecimal referencePrice,
        Integer enabled
    ) {
        FoodItem food = current == null ? new FoodItem() : current;
        String resolvedName = normalizeName(name);
        food.setName(resolvedName);
        food.setEmoji(emoji);
        String resolvedOrderUrl = orderLinkResolver.resolvePersistedUrl(
            resolvedName,
            orderUrl,
            steps,
            referencePrice
        );
        food.setSteps(steps);
        food.setImageUrl(imageUrl);
        food.setOrderUrl(resolvedOrderUrl);
        food.setWeight(resolveWeight(current, weight));
        food.setReferencePrice(referencePrice);
        food.setEnabled(resolveEnabled(current, enabled));
        if (food.getEnabled() == null) {
            food.setEnabled(1);
        }
        if (food.getDeleted() == null) {
            food.setDeleted(0);
        }
        return food;
    }

    private ImportAction importFood(FoodImportItemRequest item, FoodItem current, Map<Long, List<Long>> tagMap) {
        List<Long> nextTagIds = normalizeTagIds(item.getTagIds());
        if (current == null) {
            FoodItem food = buildFood(null, item);
            foodItemMapper.insert(food);
            replaceTags(food.getId(), nextTagIds);
            tagMap.put(food.getId(), nextTagIds);
            return ImportAction.CREATED;
        }
        List<Long> currentTagIds = tagMap.getOrDefault(current.getId(), Collections.emptyList());
        if (isSameFood(current, item, currentTagIds, nextTagIds)) {
            return ImportAction.SKIPPED;
        }
        FoodItem food = buildFood(current, item);
        foodItemMapper.updateById(food);
        replaceTags(current.getId(), nextTagIds);
        tagMap.put(current.getId(), nextTagIds);
        return ImportAction.UPDATED;
    }

    private boolean isSameFood(
        FoodItem current,
        FoodImportItemRequest item,
        List<Long> currentTagIds,
        List<Long> nextTagIds
    ) {
        FoodItem next = buildFood(copyFood(current), item);
        return Objects.equals(current.getName(), next.getName())
            && Objects.equals(current.getEmoji(), next.getEmoji())
            && Objects.equals(current.getSteps(), next.getSteps())
            && Objects.equals(current.getImageUrl(), next.getImageUrl())
            && Objects.equals(current.getOrderUrl(), next.getOrderUrl())
            && Objects.equals(current.getWeight(), next.getWeight())
            && Objects.equals(current.getReferencePrice(), next.getReferencePrice())
            && Objects.equals(current.getEnabled(), next.getEnabled())
            && Objects.equals(normalizeTagIds(currentTagIds), nextTagIds);
    }

    private FoodItem copyFood(FoodItem current) {
        FoodItem copy = new FoodItem();
        copy.setId(current.getId());
        copy.setName(current.getName());
        copy.setEmoji(current.getEmoji());
        copy.setSteps(current.getSteps());
        copy.setImageUrl(current.getImageUrl());
        copy.setOrderUrl(current.getOrderUrl());
        copy.setWeight(current.getWeight());
        copy.setReferencePrice(current.getReferencePrice());
        copy.setEnabled(current.getEnabled());
        copy.setDeleted(current.getDeleted());
        return copy;
    }

    private String normalizeName(String name) {
        return name == null ? null : name.trim();
    }

    private Integer resolveWeight(FoodItem current, Integer weight) {
        if (weight != null) {
            return weight;
        }
        if (current != null && current.getWeight() != null) {
            return current.getWeight();
        }
        return 80;
    }

    private Integer resolveEnabled(FoodItem current, Integer enabled) {
        if (enabled != null) {
            return enabled;
        }
        if (current != null && current.getEnabled() != null) {
            return current.getEnabled();
        }
        return 1;
    }

    private List<Long> normalizeTagIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Collections.emptyList();
        }
        return tagIds.stream().filter(Objects::nonNull).distinct().toList();
    }

    private void replaceTags(Long foodId, List<Long> tagIds) {
        // 这里直接做全量替换, 比逐条 diff 更简单, 也更符合当前单人小系统场景。
        deleteRelations(foodId);
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        tagIds.stream().distinct().map(tagId -> buildRelation(foodId, tagId)).forEach(foodItemTagRelMapper::insert);
    }

    private void deleteRelations(Long foodId) {
        LambdaQueryWrapper<FoodItemTagRel> query = new LambdaQueryWrapper<>();
        query.eq(FoodItemTagRel::getFoodId, foodId);
        foodItemTagRelMapper.delete(query);
    }

    private FoodItemTagRel buildRelation(Long foodId, Long tagId) {
        FoodItemTagRel relation = new FoodItemTagRel();
        relation.setFoodId(foodId);
        relation.setTagId(tagId);
        return relation;
    }

    private enum ImportAction {
        CREATED,
        UPDATED,
        SKIPPED
    }
}
