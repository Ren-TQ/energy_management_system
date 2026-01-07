package com.campus.energy.controller;

import com.campus.energy.dto.DeviceDTO;
import com.campus.energy.dto.common.Result;
import com.campus.energy.enums.DeviceStatus;
import com.campus.energy.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备信息控制器
 */
@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
@Tag(name = "设备管理", description = "智能电表设备的增删改查接口")
public class DeviceController {
    
    private final DeviceService deviceService;
    
    @GetMapping
    @Operation(summary = "获取所有设备列表")
    public Result<List<DeviceDTO>> getAllDevices() {
        return Result.success(deviceService.getAllDevices());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取设备信息")
    public Result<DeviceDTO> getDeviceById(
            @Parameter(description = "设备ID") @PathVariable Long id) {
        return Result.success(deviceService.getDeviceById(id));
    }
    
    @GetMapping("/building/{buildingId}")
    @Operation(summary = "根据建筑ID获取设备列表")
    public Result<List<DeviceDTO>> getDevicesByBuildingId(
            @Parameter(description = "建筑ID") @PathVariable Long buildingId) {
        return Result.success(deviceService.getDevicesByBuildingId(buildingId));
    }
    
    @GetMapping("/online")
    @Operation(summary = "获取所有在线设备")
    public Result<List<DeviceDTO>> getOnlineDevices() {
        return Result.success(deviceService.getOnlineDevices());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建设备", description = "需要管理员权限")
    public Result<DeviceDTO> createDevice(@Valid @RequestBody DeviceDTO dto) {
        return Result.success("创建成功", deviceService.createDevice(dto));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新设备信息", description = "需要管理员权限")
    public Result<DeviceDTO> updateDevice(
            @Parameter(description = "设备ID") @PathVariable Long id,
            @Valid @RequestBody DeviceDTO dto) {
        return Result.success("更新成功", deviceService.updateDevice(id, dto));
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新设备状态", description = "需要管理员权限")
    public Result<DeviceDTO> updateDeviceStatus(
            @Parameter(description = "设备ID") @PathVariable Long id,
            @Parameter(description = "设备状态") @RequestParam DeviceStatus status) {
        return Result.success("状态更新成功", deviceService.updateDeviceStatus(id, status));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除设备", description = "需要管理员权限")
    public Result<Void> deleteDevice(
            @Parameter(description = "设备ID") @PathVariable Long id) {
        deviceService.deleteDevice(id);
        return Result.success("删除成功", null);
    }
}

