package com.campus.energy.controller;

import com.campus.energy.dto.StatisticsDTO;
import com.campus.energy.dto.common.Result;
import com.campus.energy.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计数据控制器
 */
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Tag(name = "统计数据", description = "系统统计数据接口")
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @GetMapping("/overview")
    @Operation(summary = "获取系统概览统计数据")
    public Result<StatisticsDTO> getOverviewStatistics() {
        return Result.success(statisticsService.getOverviewStatistics());
    }
}

