package com.campus.energy.controller;

import com.campus.energy.dto.EnergyDataDTO;
import com.campus.energy.dto.common.Result;
import com.campus.energy.service.EnergyDataService;
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

/**
 * ============================================
 * MVC架构 - Controller层（控制器层）
 * ============================================
 * 
 * 架构说明：
 * 本类属于MVC架构中的Controller（C）层，负责处理能耗数据相关的HTTP请求
 * 
 * MVC职责划分：
 * - Controller (C): 本类 - 处理HTTP请求和响应
 * - Model (M): EnergyDataService、EnergyDataRepository、EnergyData实体
 * - View (V): Result<T> JSON响应格式
 * ============================================
 * 
 * 能耗数据控制器
 * 
 * <p>负责处理智能电表能耗数据相关的HTTP请求，提供能耗数据的查询、统计等功能。
 * 能耗数据是系统的核心数据，包括电压、电流、功率、累计用电量等信息。</p>
 * 
 * <p><b>功能说明：</b></p>
 * <ul>
 *   <li>数据查询：支持按设备查询、按时间范围查询、查询最新数据等</li>
 *   <li>数据统计：支持计算用电量、统计今日数据等</li>
 *   <li>分页支持：大数据量查询支持分页，提高性能</li>
 * </ul>
 * 
 * <p><b>权限说明：</b></p>
 * <ul>
 *   <li>所有接口：已登录用户均可访问</li>
 * </ul>
 * 
 * <p><b>数据格式：</b></p>
 * <ul>
 *   <li>电压单位：伏特（V）</li>
 *   <li>电流单位：安培（A）</li>
 *   <li>功率单位：瓦特（W）</li>
 *   <li>用电量单位：千瓦时（kWh）</li>
 * </ul>
 * 
 * <p><b>请求路径：</b>/api/energy-data</p>
 * 
 * @author Campus Energy System
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/energy-data")
@RequiredArgsConstructor
@Tag(name = "能耗数据", description = "能耗数据查询接口")
public class EnergyDataController {
    
    /** 能耗数据服务层，处理能耗数据相关的业务逻辑 */
    private final EnergyDataService energyDataService;
    
    /**
     * 根据设备ID获取能耗数据（分页）
     * 
     * <p>查询指定设备的历史能耗数据，支持分页查询，默认每页20条，按采集时间倒序排列。
     * 适用于设备历史数据查看、数据导出等场景。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/energy-data/device/1?page=0&size=20&sort=collectTime,desc</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>deviceId：设备ID，路径参数，必填</li>
     *   <li>page：页码，查询参数，可选，默认0（从0开始）</li>
     *   <li>size：每页数量，查询参数，可选，默认20</li>
     *   <li>sort：排序字段，查询参数，可选，默认按collectTime倒序</li>
     * </ul>
     * 
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": {
     *     "content": [
     *       {
     *         "id": 1,
     *         "deviceId": 1,
     *         "voltage": 220.5,
     *         "current": 4.5,
     *         "power": 990.0,
     *         "totalEnergy": 150.5,
     *         "collectTime": "2025-01-01T10:00:00"
     *       }
     *     ],
     *     "totalElements": 100,
     *     "totalPages": 5,
     *     "number": 0,
     *     "size": 20
     *   },
     *   "timestamp": 1704067200000
     * }
     * </pre>
     * 
     * @param deviceId 设备ID，从URL路径中获取
     * @param pageable 分页参数，Spring Data自动注入，支持page、size、sort等参数
     * @return 包含分页能耗数据的Result对象
     */
    @GetMapping("/device/{deviceId}")
    @Operation(summary = "根据设备ID获取能耗数据（分页）", description = "查询指定设备的历史能耗数据，支持分页查询")
    public Result<Page<EnergyDataDTO>> getEnergyDataByDeviceId(
            @Parameter(description = "设备ID", required = true, example = "1") @PathVariable Long deviceId,
            @PageableDefault(size = 20, sort = "collectTime") Pageable pageable) {
        return Result.success(energyDataService.getEnergyDataByDeviceId(deviceId, pageable));
    }
    
    /**
     * 获取设备最新的能耗数据
     * 
     * <p>查询指定设备最近一次采集的能耗数据。适用于实时监控、仪表盘展示等场景。
     * 如果设备没有数据，返回404错误。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/energy-data/device/1/latest</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>deviceId：设备ID，路径参数，必填</li>
     * </ul>
     * 
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": {
     *     "id": 100,
     *     "deviceId": 1,
     *     "voltage": 220.5,
     *     "current": 4.5,
     *     "power": 990.0,
     *     "totalEnergy": 150.5,
     *     "collectTime": "2025-01-01T10:00:00"
     *   },
     *   "timestamp": 1704067200000
     * }
     * </pre>
     * 
     * <p><b>异常情况：</b></p>
     * <ul>
     *   <li>设备不存在：返回404错误</li>
     *   <li>设备暂无数据：返回404错误，提示"暂无能耗数据"</li>
     * </ul>
     * 
     * @param deviceId 设备ID，从URL路径中获取
     * @return 包含最新能耗数据的Result对象，如果无数据则返回404
     */
    @GetMapping("/device/{deviceId}/latest")
    @Operation(summary = "获取设备最新的能耗数据", description = "查询指定设备最近一次采集的能耗数据，用于实时监控")
    public Result<EnergyDataDTO> getLatestEnergyData(
            @Parameter(description = "设备ID", required = true, example = "1") @PathVariable Long deviceId) {
        return energyDataService.getLatestEnergyData(deviceId)
                .map(Result::success)
                .orElse(Result.notFound("暂无能耗数据"));
    }
    
    /**
     * 获取设备在指定时间范围内的能耗数据
     * 
     * <p>查询指定设备在某个时间段内的所有能耗数据。适用于按时间段分析、数据报表生成等场景。
     * 时间格式为ISO 8601标准格式：yyyy-MM-ddTHH:mm:ss</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/energy-data/device/1/range?startTime=2025-01-01T00:00:00&endTime=2025-01-01T23:59:59</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>deviceId：设备ID，路径参数，必填</li>
     *   <li>startTime：开始时间，查询参数，必填，格式：yyyy-MM-ddTHH:mm:ss</li>
     *   <li>endTime：结束时间，查询参数，必填，格式：yyyy-MM-ddTHH:mm:ss</li>
     * </ul>
     * 
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>时间范围不能超过合理范围（建议不超过1个月）</li>
     *   <li>结束时间必须晚于开始时间</li>
     *   <li>如果时间范围内数据量很大，建议使用分页接口</li>
     * </ul>
     * 
     * @param deviceId 设备ID，从URL路径中获取
     * @param startTime 开始时间，从查询参数中获取，ISO 8601格式
     * @param endTime 结束时间，从查询参数中获取，ISO 8601格式
     * @return 包含时间范围内所有能耗数据的Result对象
     */
    @GetMapping("/device/{deviceId}/range")
    @Operation(summary = "获取设备在指定时间范围内的能耗数据", description = "查询指定设备在某个时间段内的所有能耗数据")
    public Result<List<EnergyDataDTO>> getEnergyDataByTimeRange(
            @Parameter(description = "设备ID", required = true, example = "1") @PathVariable Long deviceId,
            @Parameter(description = "开始时间", required = true, example = "2025-01-01T00:00:00") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间", required = true, example = "2025-01-01T23:59:59") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return Result.success(energyDataService.getEnergyDataByTimeRange(deviceId, startTime, endTime));
    }
    
    /**
     * 获取设备今日能耗数据
     * 
     * <p>查询指定设备从今天00:00:00到当前时间的所有能耗数据。
     * 适用于今日数据统计、今日用电分析等场景。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/energy-data/device/1/today</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>deviceId：设备ID，路径参数，必填</li>
     * </ul>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>今日用电量统计</li>
     *   <li>今日用电趋势分析</li>
     *   <li>实时监控今日数据</li>
     * </ul>
     * 
     * @param deviceId 设备ID，从URL路径中获取
     * @return 包含今日所有能耗数据的Result对象
     */
    @GetMapping("/device/{deviceId}/today")
    @Operation(summary = "获取设备今日能耗数据", description = "查询指定设备从今天00:00:00到当前时间的所有能耗数据")
    public Result<List<EnergyDataDTO>> getTodayEnergyData(
            @Parameter(description = "设备ID", required = true, example = "1") @PathVariable Long deviceId) {
        return Result.success(energyDataService.getTodayEnergyData(deviceId));
    }
    
    /**
     * 获取所有设备的最新能耗数据
     * 
     * <p>查询系统中所有设备最近一次采集的能耗数据。适用于系统概览、实时监控大屏等场景。
     * 返回所有设备的最新状态，方便一次性获取全系统实时数据。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/energy-data/latest-all</pre>
     * 
     * <p><b>响应说明：</b></p>
     * <ul>
     *   <li>返回所有设备的最新能耗数据列表</li>
     *   <li>如果某个设备没有数据，该设备不会出现在列表中</li>
     *   <li>数据按设备ID排序</li>
     * </ul>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>系统实时监控大屏</li>
     *   <li>设备状态概览</li>
     *   <li>批量设备监控</li>
     * </ul>
     * 
     * @return 包含所有设备最新能耗数据的Result对象
     */
    @GetMapping("/latest-all")
    @Operation(summary = "获取所有设备的最新能耗数据", description = "查询系统中所有设备最近一次采集的能耗数据，用于实时监控")
    public Result<List<EnergyDataDTO>> getLatestEnergyDataForAllDevices() {
        return Result.success(energyDataService.getLatestEnergyDataForAllDevices());
    }
    
    /**
     * 计算设备在指定时间范围内的用电量
     * 
     * <p>计算指定设备在某个时间段内的总用电量（kWh）。通过累计用电量的差值来计算。
     * 适用于电费计算、用电量统计等场景。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/energy-data/device/1/consumption?startTime=2025-01-01T00:00:00&endTime=2025-01-01T23:59:59</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>deviceId：设备ID，路径参数，必填</li>
     *   <li>startTime：开始时间，查询参数，必填，格式：yyyy-MM-ddTHH:mm:ss</li>
     *   <li>endTime：结束时间，查询参数，必填，格式：yyyy-MM-ddTHH:mm:ss</li>
     * </ul>
     * 
     * <p><b>计算方式：</b></p>
     * <ul>
     *   <li>用电量 = 结束时间的累计用电量 - 开始时间的累计用电量</li>
     *   <li>如果开始时间或结束时间没有数据，返回0.0</li>
     *   <li>单位：千瓦时（kWh）</li>
     * </ul>
     * 
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": 25.5,
     *   "timestamp": 1704067200000
     * }
     * </pre>
     * 
     * @param deviceId 设备ID，从URL路径中获取
     * @param startTime 开始时间，从查询参数中获取，ISO 8601格式
     * @param endTime 结束时间，从查询参数中获取，ISO 8601格式
     * @return 包含用电量（kWh）的Result对象，如果无法计算则返回0.0
     */
    @GetMapping("/device/{deviceId}/consumption")
    @Operation(summary = "计算设备在指定时间范围内的用电量", description = "计算指定设备在某个时间段内的总用电量（kWh）")
    public Result<Double> calculateEnergyConsumption(
            @Parameter(description = "设备ID", required = true, example = "1") @PathVariable Long deviceId,
            @Parameter(description = "开始时间", required = true, example = "2025-01-01T00:00:00") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间", required = true, example = "2025-01-01T23:59:59") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        Double consumption = energyDataService.calculateEnergyConsumption(deviceId, startTime, endTime);
        return Result.success(consumption != null ? consumption : 0.0);
    }
}

