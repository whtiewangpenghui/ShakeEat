package com.wdd.shakeeat.module.shake.controller;

import com.wdd.shakeeat.common.api.ApiResponse;
import com.wdd.shakeeat.module.shake.dto.RejectShakeRequest;
import com.wdd.shakeeat.module.shake.dto.ShakeRequest;
import com.wdd.shakeeat.module.shake.dto.ShakeQuotaResponse;
import com.wdd.shakeeat.module.shake.dto.ShakeResponse;
import com.wdd.shakeeat.module.shake.service.ShakeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shake")
@RequiredArgsConstructor
public class ShakeController {

    private final ShakeService shakeService;

    @PostMapping
    public ApiResponse<ShakeResponse> shake(@Valid @RequestBody ShakeRequest request) {
        return ApiResponse.ok(shakeService.shake(request));
    }

    @GetMapping("/quota")
    public ApiResponse<ShakeQuotaResponse> getQuota() {
        return ApiResponse.ok(shakeService.getQuota());
    }

    @PostMapping("/{historyId}/accept")
    public ApiResponse<Void> accept(@PathVariable Long historyId) {
        shakeService.accept(historyId);
        return ApiResponse.ok("已确认", null);
    }

    @PostMapping("/{historyId}/reject")
    public ApiResponse<Void> reject(@PathVariable Long historyId, @Valid @RequestBody RejectShakeRequest request) {
        shakeService.reject(historyId, request.getRejectFeedbackCode());
        return ApiResponse.ok("已拒绝", null);
    }
}
