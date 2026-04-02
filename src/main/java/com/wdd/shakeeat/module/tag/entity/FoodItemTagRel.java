package com.wdd.shakeeat.module.tag.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("food_item_tag_rel")
public class FoodItemTagRel {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long foodId;
    private Long tagId;
}
