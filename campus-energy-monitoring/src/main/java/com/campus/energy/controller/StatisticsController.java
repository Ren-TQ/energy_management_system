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
 * 
 * <p>负责处理系统统计数据相关的HTTP请求，提供系统概览、统计分析等功能。
 * 统计数据用于展示系统的整体运行状况，包括设备统计、能耗统计、告警统计等。</p>
 * 
 * <p><b>功能说明：</b></p>
 * <ul>
 *   <li>系统概览：提供系统整体统计数据，包括设备总数、在线设备数、总用电量、告警数量等</li>
 *   <li>数据统计：支持按建筑、按设备、按时间等维度进行统计分析</li>
 * </ul>
 * 
 * <p><b>权限说明：</b></p>
 * <ul>
 *   <li>所有接口：已登录用户均可访问</li>
 * </ul>
 * 
 * <p><b>请求路径：</b>/api/statistics</p>
 * 
 * @author Campus Energy System
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Tag(name = "统计数据", description = "系统统计数据接口")
public class StatisticsController {
    
    /** 统计服务层，处理统计数据相关的业务逻辑 */
    private final StatisticsService statisticsService;
    
    /**
     * 获取系统概览统计数据
     * 
     * <p>查询系统的整体统计数据，包括：
     * <ul>
     *   <li>设备统计：设备总数、在线设备数、离线设备数等</li>
     *   <li>建筑统计：建筑总数、各类型建筑数量等</li>
     *   <li>能耗统计：总用电量、今日用电量、平均功率等</li>
     *   <li>告警统计：告警总数、未处理告警数、今日告警数等</li>
     * </ul>
     * 适用于系统仪表盘、首页概览等场景。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/statistics/overview</pre>
     * 
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": {
     *     "totalDevices": 10,
     *     "onlineDevices": 8,
     *     "offlineDevices": 2,
     *     "totalBuildings": 4,
     *     "totalEnergyConsumption": 1500.5,
     *     "todayEnergyConsumption": 125.3,
     *     "totalAlerts": 25,
     *     "unresolvedAlerts": 5,
     *     "buildingEnergyStats": [
     *       {
     *         "buildingId": 1,
     *         "buildingName": "楸苑宿舍三号楼",
     *         "totalConsumption": 500.2
     *       }
     *     ]
     *   },
     *   "timestamp": 1704067200000
     * }
     * </pre>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>系统仪表盘首页</li>
     *   <li>系统概览展示</li>
     *   <li>数据大屏展示</li>
     * </ul>
     * 
     * @return 包含系统概览统计数据的Result对象
     */
    @GetMapping("/overview")
    @Operation(summary = "获取系统概览统计数据", description = "查询系统的整体统计数据，包括设备、建筑、能耗、告警等统计信息")
    public Result<StatisticsDTO> getOverviewStatistics() {
        return Result.success(statisticsService.getOverviewStatistics());
    }
}

