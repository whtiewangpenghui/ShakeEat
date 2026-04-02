package com.wdd.shakeeat.module.preference.service;

import com.wdd.shakeeat.module.preference.dto.PreferenceResponse;
import com.wdd.shakeeat.module.preference.dto.PreferenceUpdateRequest;

public interface PreferenceService {

    PreferenceResponse getPreference();

    PreferenceResponse updatePreference(PreferenceUpdateRequest request);
}
