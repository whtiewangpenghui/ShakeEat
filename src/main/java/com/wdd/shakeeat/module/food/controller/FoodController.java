package com.wdd.shakeeat.module.food.controller;

import com.wdd.shakeeat.common.api.ApiResponse;
import com.wdd.shakeeat.module.food.dto.FoodDetailResponse;
import com.wdd.shakeeat.module.food.dto.FoodEnabledRequest;
import com.wdd.shakeeat.module.food.dto.FoodExportItemResponse;
import com.wdd.shakeeat.module.food.dto.FoodImportRequest;
import com.wdd.shakeeat.module.food.dto.FoodImportResponse;
import com.wdd.shakeeat.module.food.dto.FoodListResponse;
import com.wdd.shakeeat.module.food.dto.FoodSaveRequest;
import com.wdd.shakeeat.module.food.service.FoodService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @GetMapping
    public ApiResponse<List<FoodListResponse>> listFoods() {
        return ApiResponse.ok(foodService.listFoods());
    }

    @GetMapping("/export")
    public ApiResponse<List<FoodExportItemResponse>> exportFoods() {
        return ApiResponse.ok(foodService.exportFoods());
    }

    @GetMapping("/{id}")
    public ApiResponse<FoodDetailResponse> getFood(@PathVariable Long id) {
        return ApiResponse.ok(foodService.getFood(id));
    }

    @PostMapping
    public ApiResponse<FoodDetailResponse> createFood(@Valid @RequestBody FoodSaveRequest request) {
        return ApiResponse.ok("创建成功", foodService.createFood(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<FoodDetailResponse> updateFood(@PathVariable Long id, @Valid @RequestBody FoodSaveRequest request) {
        return ApiResponse.ok("更新成功", foodService.updateFood(id, request));
    }

    @PostMapping("/import")
    public ApiResponse<FoodImportResponse> importFoods(@Valid @RequestBody FoodImportRequest request) {
        return ApiResponse.ok("导入成功", foodService.importFoods(request.getItems()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
        return ApiResponse.ok("删除成功", null);
    }

    @PatchMapping("/{id}/enable")
    public ApiResponse<Void> updateEnabled(@PathVariable Long id, @Valid @RequestBody FoodEnabledRequest request) {
        foodService.updateEnabled(id, request.isEnabled());
        return ApiResponse.ok("状态更新成功", null);
    }
}
