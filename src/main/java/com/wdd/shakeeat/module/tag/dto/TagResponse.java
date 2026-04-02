package com.wdd.shakeeat.module.tag.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TagResponse {

    private Long id;
    private String name;
    private String icon;
    private String tagType;
    private Integer sort;
    private Integer enabled;
}
