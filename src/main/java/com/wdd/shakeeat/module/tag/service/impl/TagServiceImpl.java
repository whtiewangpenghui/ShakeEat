package com.wdd.shakeeat.module.tag.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wdd.shakeeat.common.exception.BusinessException;
import com.wdd.shakeeat.module.tag.dto.TagSaveRequest;
import com.wdd.shakeeat.module.tag.dto.TagResponse;
import com.wdd.shakeeat.module.tag.entity.FoodTag;
import com.wdd.shakeeat.module.tag.entity.FoodItemTagRel;
import com.wdd.shakeeat.module.tag.mapper.FoodItemTagRelMapper;
import com.wdd.shakeeat.module.tag.mapper.FoodTagMapper;
import com.wdd.shakeeat.module.tag.service.TagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final FoodTagMapper foodTagMapper;
    private final FoodItemTagRelMapper foodItemTagRelMapper;

    @Override
    public List<TagResponse> listEnabledTags() {
        return foodTagMapper.selectList(buildQuery()).stream().map(this::toResponse).toList();
    }

    @Override
    public List<TagResponse> listAllTags() {
        return foodTagMapper.selectList(buildAllQuery()).stream().map(this::toResponse).toList();
    }

    @Override
    public TagResponse createTag(TagSaveRequest request) {
        ensureNameUnique(request.getName(), null);
        FoodTag tag = buildTag(null, request);
        tag.setTagType("CUSTOM");
        foodTagMapper.insert(tag);
        return toResponse(tag);
    }

    @Override
    public TagResponse updateTag(Long id, TagSaveRequest request) {
        FoodTag tag = getTag(id);
        ensureNameUnique(request.getName(), id);
        fillTag(tag, request);
        foodTagMapper.updateById(tag);
        return toResponse(tag);
    }

    @Override
    public void deleteTag(Long id) {
        FoodTag tag = getTag(id);
        // 系统标签直接参与模式筛选, 删除会破坏核心推荐逻辑, 这里只允许保留。
        if ("SYSTEM".equals(tag.getTagType())) {
            throw new BusinessException("系统标签不允许删除");
        }
        // 已被食物引用的标签先不允许删, 避免把菜单库关系打断。
        if (isTagInUse(id)) {
            throw new BusinessException("标签已被食物使用, 不能删除");
        }
        tag.setDeleted(1);
        tag.setEnabled(0);
        foodTagMapper.updateById(tag);
    }

    private LambdaQueryWrapper<FoodTag> buildQuery() {
        LambdaQueryWrapper<FoodTag> query = new LambdaQueryWrapper<>();
        query.eq(FoodTag::getEnabled, 1);
        query.eq(FoodTag::getDeleted, 0);
        query.orderByAsc(FoodTag::getSort, FoodTag::getId);
        return query;
    }

    private LambdaQueryWrapper<FoodTag> buildAllQuery() {
        LambdaQueryWrapper<FoodTag> query = new LambdaQueryWrapper<>();
        query.eq(FoodTag::getDeleted, 0);
        query.orderByAsc(FoodTag::getSort, FoodTag::getId);
        return query;
    }

    private TagResponse toResponse(FoodTag tag) {
        return TagResponse.builder()
            .id(tag.getId())
            .name(tag.getName())
            .icon(tag.getIcon())
            .tagType(tag.getTagType())
            .sort(tag.getSort())
            .enabled(tag.getEnabled())
            .build();
    }

    private FoodTag getTag(Long id) {
        FoodTag tag = foodTagMapper.selectById(id);
        if (tag == null || Integer.valueOf(1).equals(tag.getDeleted())) {
            throw new BusinessException("标签不存在");
        }
        return tag;
    }

    private void ensureNameUnique(String name, Long excludeId) {
        LambdaQueryWrapper<FoodTag> query = new LambdaQueryWrapper<>();
        query.eq(FoodTag::getName, name);
        query.eq(FoodTag::getDeleted, 0);
        if (excludeId != null) {
            query.ne(FoodTag::getId, excludeId);
        }
        if (foodTagMapper.selectCount(query) > 0) {
            throw new BusinessException("标签名称已存在");
        }
    }

    private FoodTag buildTag(FoodTag current, TagSaveRequest request) {
        FoodTag tag = current == null ? new FoodTag() : current;
        fillTag(tag, request);
        if (tag.getEnabled() == null) {
            tag.setEnabled(1);
        }
        if (tag.getDeleted() == null) {
            tag.setDeleted(0);
        }
        return tag;
    }

    private void fillTag(FoodTag tag, TagSaveRequest request) {
        tag.setName(request.getName());
        tag.setIcon(request.getIcon());
        tag.setSort(request.getSort());
        tag.setEnabled(request.getEnabled());
    }

    private boolean isTagInUse(Long tagId) {
        LambdaQueryWrapper<FoodItemTagRel> query = new LambdaQueryWrapper<>();
        query.eq(FoodItemTagRel::getTagId, tagId);
        return foodItemTagRelMapper.selectCount(query) > 0;
    }
}
