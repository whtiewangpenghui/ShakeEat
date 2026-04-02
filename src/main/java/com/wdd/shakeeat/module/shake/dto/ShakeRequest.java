package com.wdd.shakeeat.module.shake.dto;

import com.wdd.shakeeat.module.shake.enums.ShakeMode;
import com.wdd.shakeeat.module.shake.enums.TriggerType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShakeRequest {

    @NotNull(message = "mode 不能为空")
    private ShakeMode mode;

    @NotNull(message = "triggerType 不能为空")
    private TriggerType triggerType;
}
