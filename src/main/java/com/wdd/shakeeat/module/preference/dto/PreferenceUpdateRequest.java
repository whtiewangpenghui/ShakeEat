package com.wdd.shakeeat.module.preference.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreferenceUpdateRequest {

    private List<Long> favoriteTagIds;
    private List<Long> avoidTagIds;
    private BigDecimal maxBudget;

    @NotBlank(message = "lastMode 不能为空")
    private String lastMode;
}
