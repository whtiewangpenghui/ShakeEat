package com.wdd.shakeeat.module.tag.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("food_tag")
public class FoodTag {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String icon;
    private String tagType;
    private Integer sort;
    private Integer enabled;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
