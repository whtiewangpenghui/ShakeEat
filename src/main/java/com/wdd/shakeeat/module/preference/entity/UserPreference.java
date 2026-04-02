package com.wdd.shakeeat.module.preference.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("user_preference")
public class UserPreference {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String favoriteTagIds;
    private String avoidTagIds;
    private BigDecimal maxBudget;
    private String lastMode;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
