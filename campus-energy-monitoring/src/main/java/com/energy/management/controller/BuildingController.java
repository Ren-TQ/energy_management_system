package com.energy.management.controller;

import com.energy.management.dto.request.BuildingRequest;
import com.energy.management.dto.response.ApiResponse;
import com.energy.management.dto.response.BuildingResponse;
import com.energy.management.service.BuildingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/buildings")
@Tag(name = "建筑管理", description = "校园建筑信息管理接口")
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @PostMapping
    @Operation(summary = "创建建筑")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BuildingResponse> createBuilding(
            @Valid @RequestBody BuildingRequest request) {
        BuildingResponse response = buildingService.createBuilding(request);
        return ApiResponse.success("创建成功", response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新建筑信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BuildingResponse> updateBuilding(
            @PathVariable Long id,
            @Valid @RequestBody BuildingRequest request) {
        BuildingResponse response = buildingService.updateBuilding(id, request);
        return ApiResponse.success("更新成功", response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除建筑")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> deleteBuilding(@PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取建筑详情")
    public ApiResponse<BuildingResponse> getBuilding(@PathVariable Long id) {
        BuildingResponse response = buildingService.getBuildingById(id);
        return ApiResponse.success("查询成功", response);
    }

    @GetMapping
    @Operation(summary = "获取建筑列表（分页）")
    public ApiResponse<Page<BuildingResponse>> getBuildings(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<BuildingResponse> response = buildingService.getAllBuildings(pageable);
        return ApiResponse.success("查询成功", response);
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有建筑列表")
    public ApiResponse<List<BuildingResponse>> getAllBuildings() {
        List<BuildingResponse> response = buildingService.getAllBuildings();
        return ApiResponse.success("查询成功", response);
    }
}