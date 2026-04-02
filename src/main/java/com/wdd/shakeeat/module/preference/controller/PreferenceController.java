package com.wdd.shakeeat.module.preference.controller;

import com.wdd.shakeeat.common.api.ApiResponse;
import com.wdd.shakeeat.module.preference.dto.PreferenceResponse;
import com.wdd.shakeeat.module.preference.dto.PreferenceUpdateRequest;
import com.wdd.shakeeat.module.preference.service.PreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceService preferenceService;

    @GetMapping
    public ApiResponse<PreferenceResponse> getPreference() {
        return ApiResponse.ok(preferenceService.getPreference());
    }

    @PutMapping
    public ApiResponse<PreferenceResponse> updatePreference(@Valid @RequestBody PreferenceUpdateRequest request) {
        return ApiResponse.ok("保存成功", preferenceService.updatePreference(request));
    }
}
