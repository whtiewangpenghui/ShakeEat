package com.wdd.shakeeat.module.shake.service;

import com.wdd.shakeeat.module.shake.dto.ShakeRequest;
import com.wdd.shakeeat.module.shake.dto.ShakeQuotaResponse;
import com.wdd.shakeeat.module.shake.dto.ShakeResponse;

public interface ShakeService {

    ShakeResponse shake(ShakeRequest request);

    void accept(Long historyId);

    void reject(Long historyId, String rejectFeedbackCode);

    ShakeQuotaResponse getQuota();
}
