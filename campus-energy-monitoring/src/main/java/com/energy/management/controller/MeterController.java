package com.energy.management.controller;

import com.energy.management.dto.request.MeterRequest;
import com.energy.management.dto.response.ApiResponse;
import com.energy.management.dto.response.MeterResponse;
import com.energy.management.service.MeterService;
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
@RequestMapping("/api/meters")
@Tag(name = "设备管理", description = "智能电表设备管理接口")
public class MeterController {

    @Autowired
    private MeterService meterService;

    @PostMapping
    @Operation(summary = "创建设备")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<MeterResponse> createMeter(
            @Valid @RequestBody MeterRequest request) {
        MeterResponse response = meterService.createMeter(request);
        return ApiResponse.success("创建成功", response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新设备信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<MeterResponse> updateMeter(
            @PathVariable Long id,
            @Valid @RequestBody MeterRequest request) {
        MeterResponse response = meterService.updateMeter(id, request);
        return ApiResponse.success("更新成功", response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除设备")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> deleteMeter(@PathVariable Long id) {
        meterService.deleteMeter(id);
        return ApiResponse.success("删除成功", null);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "停用设备")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> deactivateMeter(@PathVariable Long id) {
        meterService.deactivateMeter(id);
        return ApiResponse.success("停用成功", null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取设备详情")
    public ApiResponse<MeterResponse> getMeter(@PathVariable Long id) {
        MeterResponse response = meterService.getMeterById(id);
        return ApiResponse.success("查询成功", response);
    }

    @GetMapping("/sn/{serialNumber}")
    @Operation(summary = "根据序列号获取设备")
    public ApiResponse<MeterResponse> getMeterBySerialNumber(
            @PathVariable String serialNumber) {
        MeterResponse response = meterService.getMeterBySerialNumber(serialNumber);
        return ApiResponse.success("查询成功", response);
    }

    @GetMapping
    @Operation(summary = "获取设备列表（分页）")
    public ApiResponse<Page<MeterResponse>> getMeters(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<MeterResponse> response = meterService.getAllMeters(pageable);
        return ApiResponse.success("查询成功", response);
    }

    @GetMapping("/building/{buildingId}")
    @Operation(summary = "获取建筑下的设备列表")
    public ApiResponse<List<MeterResponse>> getMetersByBuilding(
            @PathVariable Long buildingId) {
        List<MeterResponse> response = meterService.getMetersByBuildingId(buildingId);
        return ApiResponse.success("查询成功", response);
    }
}