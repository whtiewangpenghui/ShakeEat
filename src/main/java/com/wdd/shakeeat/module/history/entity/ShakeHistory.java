package com.wdd.shakeeat.module.history.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("shake_history")
public class ShakeHistory {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long foodId;
    private String foodName;
    private String mode;
    private String triggerType;
    private String resultStatus;
    private String aiTip;
    private String aiSource;
    private String promptSnapshot;
    private String scoreSnapshot;
    private String stepsSnapshot;
    private String orderUrlSnapshot;
    private BigDecimal referencePriceSnapshot;
    private String tagSnapshot;
    private String rejectFeedbackCode;
    private LocalDateTime shakeTime;
    private LocalDateTime confirmTime;
}
