package com.wdd.shakeeat.module.tag.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagSaveRequest {

    @NotBlank(message = "name 不能为空")
    private String name;

    private String icon;

    @NotNull(message = "sort 不能为空")
    @Min(value = 0, message = "sort 不能小于 0")
    @Max(value = 9999, message = "sort 不能大于 9999")
    private Integer sort;

    @NotNull(message = "enabled 不能为空")
    private Integer enabled;
}
