package com.wdd.shakeeat.module.food.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoodEnabledRequest {

    @NotNull(message = "enabled 不能为空")
    private Boolean enabled;

    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }
}
