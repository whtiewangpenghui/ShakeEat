package com.wdd.shakeeat.module.tag.controller;

import com.wdd.shakeeat.common.api.ApiResponse;
import com.wdd.shakeeat.module.tag.dto.TagSaveRequest;
import com.wdd.shakeeat.module.tag.dto.TagResponse;
import com.wdd.shakeeat.module.tag.service.TagService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ApiResponse<List<TagResponse>> listTags() {
        return ApiResponse.ok(tagService.listEnabledTags());
    }

    @GetMapping("/all")
    public ApiResponse<List<TagResponse>> listAllTags() {
        return ApiResponse.ok(tagService.listAllTags());
    }

    @PostMapping
    public ApiResponse<TagResponse> createTag(@Valid @RequestBody TagSaveRequest request) {
        return ApiResponse.ok("创建成功", tagService.createTag(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<TagResponse> updateTag(@PathVariable Long id, @Valid @RequestBody TagSaveRequest request) {
        return ApiResponse.ok("更新成功", tagService.updateTag(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ApiResponse.ok("删除成功", null);
    }
}
