package com.wdd.shakeeat.module.tag.service;

import com.wdd.shakeeat.module.tag.dto.TagSaveRequest;
import com.wdd.shakeeat.module.tag.dto.TagResponse;
import java.util.List;

public interface TagService {

    List<TagResponse> listEnabledTags();

    List<TagResponse> listAllTags();

    TagResponse createTag(TagSaveRequest request);

    TagResponse updateTag(Long id, TagSaveRequest request);

    void deleteTag(Long id);
}
