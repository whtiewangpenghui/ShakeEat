package com.wdd.shakeeat.module.shake.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejectShakeRequest {

    @NotBlank(message = "rejectFeedbackCode 不能为空")
    private String rejectFeedbackCode;
}
