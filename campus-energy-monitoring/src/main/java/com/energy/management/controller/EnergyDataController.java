package com.energy.management.controller;

import com.energy.management.dto.EnergyDataDTO;
import com.energy.management.dto.common.Result;
import com.energy.management.service.EnergyDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/energy-data")
@RequiredArgsConstructor
@Tag(name = "能耗数据", description = "能耗数据查询接口")
public class EnergyDataController {
    
    private final EnergyDataService energyDataService;
    
    @GetMapping("/device/{deviceId}")
    @Operation(summary = "根据设备ID获取能耗数据（分页）")
    public Result<Page<EnergyDataDTO>> getEnergyDataByDeviceId(
            @Parameter(description = "设备ID") @PathVariable Long deviceId,
            @PageableDefault(size = 20, sort = "collectTime") Pageable pageable) {
        return Result.success(energyDataService.getEnergyDataByDeviceId(deviceId, pageable));
    }
    
    @GetMapping("/device/{deviceId}/latest")
    @Operation(summary = "获取设备最新的能耗数据")
    public Result<EnergyDataDTO> getLatestEnergyData(
            @Parameter(description = "设备ID") @PathVariable Long deviceId) {
        return energyDataService.getLatestEnergyData(deviceId)
                .map(Result::success)
                .orElse(Result.notFound("暂无能耗数据"));
    }
    
    @GetMapping("/device/{deviceId}/range")
    @Operation(summary = "获取设备在指定时间范围内的能耗数据")
    public Result<List<EnergyDataDTO>> getEnergyDataByTimeRange(
            @Parameter(description = "设备ID") @PathVariable Long deviceId,
            @Parameter(description = "开始时间") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return Result.success(energyDataService.getEnergyDataByTimeRange(deviceId, startTime, endTime));
    }
    
    @GetMapping("/device/{deviceId}/today")
    @Operation(summary = "获取设备今日能耗数据")
    public Result<List<EnergyDataDTO>> getTodayEnergyData(
            @Parameter(description = "设备ID") @PathVariable Long deviceId) {
        return Result.success(energyDataService.getTodayEnergyData(deviceId));
    }
    
    @GetMapping("/latest-all")
    @Operation(summary = "获取所有设备的最新能耗数据")
    public Result<List<EnergyDataDTO>> getLatestEnergyDataForAllDevices() {
        return Result.success(energyDataService.getLatestEnergyDataForAllDevices());
    }
    
    @GetMapping("/device/{deviceId}/consumption")
    @Operation(summary = "计算设备在指定时间范围内的用电量")
    public Result<Double> calculateEnergyConsumption(
            @Parameter(description = "设备ID") @PathVariable Long deviceId,
            @Parameter(description = "开始时间") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        Double consumption = energyDataService.calculateEnergyConsumption(deviceId, startTime, endTime);
        return Result.success(consumption != null ? consumption : 0.0);
    }
}
