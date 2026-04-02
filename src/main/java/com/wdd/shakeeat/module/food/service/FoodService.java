package com.wdd.shakeeat.module.food.service;

import com.wdd.shakeeat.module.food.dto.FoodDetailResponse;
import com.wdd.shakeeat.module.food.dto.FoodExportItemResponse;
import com.wdd.shakeeat.module.food.dto.FoodImportItemRequest;
import com.wdd.shakeeat.module.food.dto.FoodImportResponse;
import com.wdd.shakeeat.module.food.dto.FoodListResponse;
import com.wdd.shakeeat.module.food.dto.FoodSaveRequest;
import java.util.List;

public interface FoodService {

    List<FoodListResponse> listFoods();

    List<FoodExportItemResponse> exportFoods();

    FoodDetailResponse getFood(Long id);

    FoodDetailResponse createFood(FoodSaveRequest request);

    FoodDetailResponse updateFood(Long id, FoodSaveRequest request);

    void deleteFood(Long id);

    void updateEnabled(Long id, boolean enabled);

    FoodImportResponse importFoods(List<FoodImportItemRequest> items);
}
