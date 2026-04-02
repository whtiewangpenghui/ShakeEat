package com.wdd.shakeeat.module.food.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("food_item")
public class FoodItem {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String emoji;
    private String steps;
    private String imageUrl;
    private String orderUrl;
    private Integer weight;
    private BigDecimal referencePrice;
    private Integer enabled;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
