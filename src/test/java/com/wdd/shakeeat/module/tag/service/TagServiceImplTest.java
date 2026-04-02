package com.wdd.shakeeat.module.tag.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wdd.shakeeat.common.exception.BusinessException;
import com.wdd.shakeeat.module.tag.entity.FoodTag;
import com.wdd.shakeeat.module.tag.mapper.FoodItemTagRelMapper;
import com.wdd.shakeeat.module.tag.mapper.FoodTagMapper;
import com.wdd.shakeeat.module.tag.service.impl.TagServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private FoodTagMapper foodTagMapper;

    @Mock
    private FoodItemTagRelMapper foodItemTagRelMapper;

    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    void deleteTagShouldRejectSystemTag() {
        FoodTag tag = new FoodTag();
        tag.setId(1L);
        tag.setDeleted(0);
        tag.setTagType("SYSTEM");
        when(foodTagMapper.selectById(1L)).thenReturn(tag);

        assertThrows(BusinessException.class, () -> tagService.deleteTag(1L));
        verify(foodTagMapper, never()).updateById(any(FoodTag.class));
    }

    @Test
    void deleteTagShouldRejectUsedCustomTag() {
        FoodTag tag = new FoodTag();
        tag.setId(2L);
        tag.setDeleted(0);
        tag.setTagType("CUSTOM");
        when(foodTagMapper.selectById(2L)).thenReturn(tag);
        when(foodItemTagRelMapper.selectCount(any())).thenReturn(1L);

        assertThrows(BusinessException.class, () -> tagService.deleteTag(2L));
        verify(foodTagMapper, never()).updateById(any(FoodTag.class));
    }
}
