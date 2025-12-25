package com.energy.management.controller;

import com.energy.management.dto.chart.ChartData;
import com.energy.management.dto.chart.PowerTrendData;
import com.energy.management.dto.response.ApiResponse;
import com.energy.management.dto.response.EnergyDataResponse;
import com.energy.management.service.EnergyDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/energy-data")
@Tag(name = "能耗数据", description = "能耗数据查询与统计接口")
public class EnergyDataController {

    @Autowired
    private EnergyDataService energyDataService;

    @GetMapping("/meter/{meterId}/latest")
    @Operation(summary = "获取设备最新数据")
    public ApiResponse<EnergyDataResponse> getLatestData(
            @PathVariable Long meterId) {
        EnergyDataResponse response = energyDataService.getLatestDataByMeterId(meterId);
        return ApiResponse.success("查询成功", response);
    }

    @GetMapping("/meter/{meterId}/recent")
    @Operation(summary = "获取设备最近数据")
    public ApiResponse<List<EnergyDataResponse>> getRecentData(
            @PathVariable Long meterId,
            @RequestParam(defaultValue = "10") int limit) {
        List<EnergyDataResponse> response = energyDataService
                .getRecentDataByMeterId(meterId, limit);
        return ApiResponse.success("查询成功", response);
    }

    @GetMapping("/meter/{meterId}/history")
    @Operation(summary = "获取设备历史数据")
    public ApiResponse<List<EnergyDataResponse>> getHistoryData(
            @PathVariable Long meterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<EnergyDataResponse> response = energyDataService
                .getHistoryData(meterId, startDate, endDate);
        return ApiResponse.success("查询成功", response);
    }

    @GetMapping("/chart/power-trend")
    @Operation(summary = "获取功率趋势数据")
    public ApiResponse<PowerTrendData> getPowerTrendData(
            @RequestParam Long meterId,
            @RequestParam(defaultValue = "10") int limit) {
        PowerTrendData response = energyDataService
                .getPowerTrendData(meterId, limit);
        return ApiResponse.success("查询成功", response);
    }

    @GetMapping("/chart/energy-distribution")
    @Operation(summary = "获取能耗分布数据")
    public ApiResponse<ChartData> getEnergyDistribution() {
        ChartData response = energyDataService.getTodayEnergyDistribution();
        return ApiResponse.success("查询成功", response);
    }

    @GetMapping("/cost/today")
    @Operation(summary = "计算今日电费")
    public ApiResponse<Double> getTodayCost() {
        Double cost = energyDataService.calculateTodayCost();
        return ApiResponse.success("查询成功", cost);
    }
}