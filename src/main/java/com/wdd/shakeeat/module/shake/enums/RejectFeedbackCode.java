package com.wdd.shakeeat.module.shake.enums;

import com.wdd.shakeeat.common.exception.BusinessException;

public enum RejectFeedbackCode {

    TOO_EXPENSIVE("太贵"),
    TOO_TROUBLESOME("太麻烦"),
    WRONG_CRAVING("不想吃这个口味"),
    NOT_IN_MOOD("今天状态不对");

    private final String label;

    RejectFeedbackCode(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static RejectFeedbackCode fromCode(String code) {
        for (RejectFeedbackCode value : values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        throw new BusinessException("拒绝反馈原因不合法");
    }
}
