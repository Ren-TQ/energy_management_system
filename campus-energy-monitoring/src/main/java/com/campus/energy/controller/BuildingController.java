package com.campus.energy.controller;

import com.campus.energy.dto.BuildingDTO;
import com.campus.energy.dto.common.Result;
import com.campus.energy.service.BuildingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 建筑信息控制器
 * 
 * 注意：Controller层只负责接收请求和返回响应，业务逻辑在Service层处理
 */
@RestController
@RequestMapping("/buildings")
@RequiredArgsConstructor
@Tag(name = "建筑管理", description = "建筑信息的增删改查接口")
public class BuildingController {
    
    private final BuildingService buildingService;
    
    @GetMapping
    @Operation(summary = "获取所有建筑列表")
    public Result<List<BuildingDTO>> getAllBuildings() {
        return Result.success(buildingService.getAllBuildings());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取建筑信息")
    public Result<BuildingDTO> getBuildingById(
            @Parameter(description = "建筑ID") @PathVariable Long id) {
        return Result.success(buildingService.getBuildingById(id));
    }
    
    @GetMapping("/categories")
    @Operation(summary = "获取所有建筑分类")
    public Result<List<String>> getAllCategories() {
        return Result.success(buildingService.getAllCategories());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建建筑", description = "需要管理员权限")
    public Result<BuildingDTO> createBuilding(@Valid @RequestBody BuildingDTO dto) {
        return Result.success("创建成功", buildingService.createBuilding(dto));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新建筑信息", description = "需要管理员权限")
    public Result<BuildingDTO> updateBuilding(
            @Parameter(description = "建筑ID") @PathVariable Long id,
            @Valid @RequestBody BuildingDTO dto) {
        return Result.success("更新成功", buildingService.updateBuilding(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除建筑", description = "需要管理员权限")
    public Result<Void> deleteBuilding(
            @Parameter(description = "建筑ID") @PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return Result.success("删除成功", null);
    }
}

