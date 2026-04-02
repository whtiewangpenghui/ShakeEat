package com.wdd.shakeeat.module.preference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wdd.shakeeat.common.constant.UserConstants;
import com.wdd.shakeeat.common.exception.BusinessException;
import com.wdd.shakeeat.common.util.JsonArrayUtils;
import com.wdd.shakeeat.module.preference.dto.PreferenceResponse;
import com.wdd.shakeeat.module.preference.dto.PreferenceUpdateRequest;
import com.wdd.shakeeat.module.preference.entity.UserPreference;
import com.wdd.shakeeat.module.preference.mapper.UserPreferenceMapper;
import com.wdd.shakeeat.module.preference.service.PreferenceService;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreferenceServiceImpl implements PreferenceService {

    private final UserPreferenceMapper userPreferenceMapper;
    private final JsonArrayUtils jsonArrayUtils;

    @Override
    public PreferenceResponse getPreference() {
        return toResponse(loadPreference());
    }

    @Override
    public PreferenceResponse updatePreference(PreferenceUpdateRequest request) {
        UserPreference preference = loadPreference();
        preference.setFavoriteTagIds(jsonArrayUtils.toJson(request.getFavoriteTagIds()));
        preference.setAvoidTagIds(jsonArrayUtils.toJson(request.getAvoidTagIds()));
        preference.setMaxBudget(request.getMaxBudget());
        preference.setLastMode(request.getLastMode());
        userPreferenceMapper.updateById(preference);
        return toResponse(preference);
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

    private PreferenceResponse toResponse(UserPreference preference) {
        return PreferenceResponse.builder()
            .userId(preference.getUserId())
            .favoriteTagIds(jsonArrayUtils.parseLongList(preference.getFavoriteTagIds()))
            .avoidTagIds(jsonArrayUtils.parseLongList(preference.getAvoidTagIds()))
            .maxBudget(preference.getMaxBudget())
            .lastMode(preference.getLastMode())
            .build();
    }
}
