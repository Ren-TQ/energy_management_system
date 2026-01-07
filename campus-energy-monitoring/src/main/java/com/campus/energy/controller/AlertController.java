package com.campus.energy.controller;

import com.campus.energy.dto.AlertDTO;
import com.campus.energy.dto.common.Result;
import com.campus.energy.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * ============================================
 * MVC架构 - Controller层（控制器层）
 * ============================================
 * 
 * 架构说明：
 * 本类属于MVC架构中的Controller（C）层，负责处理告警相关的HTTP请求
 * 
 * MVC职责划分：
 * - Controller (C): 本类 - 处理HTTP请求和响应
 * - Model (M): AlertService、AlertRepository、Alert实体
 * - View (V): Result<T> JSON响应格式
 * ============================================
 * 
 * 告警记录控制器
 * 
 * <p>负责处理系统告警记录相关的HTTP请求，提供告警查询、统计、处理等功能。
 * 告警记录用于记录设备异常情况，如功率过载、电压异常、设备离线等。</p>
 * 
 * <p><b>功能说明：</b></p>
 * <ul>
 *   <li>告警查询：支持查询所有告警、按设备查询、按时间范围查询、查询未处理告警等</li>
 *   <li>告警统计：支持统计今日告警数量、未处理告警数量、告警类型统计等</li>
 *   <li>告警处理：支持标记告警为已处理（需要管理员权限）</li>
 * </ul>
 * 
 * <p><b>权限说明：</b></p>
 * <ul>
 *   <li>查询接口：所有已登录用户均可访问</li>
 *   <li>处理接口：需要管理员（ADMIN）角色</li>
 * </ul>
 * 
 * <p><b>告警类型：</b></p>
 * <ul>
 *   <li>POWER_OVERLOAD：功率过载，超过额定功率阈值</li>
 *   <li>VOLTAGE_HIGH：电压过高，超过正常范围上限</li>
 *   <li>VOLTAGE_LOW：电压过低，低于正常范围下限</li>
 *   <li>CURRENT_ABNORMAL：电流异常</li>
 *   <li>DEVICE_OFFLINE：设备离线，无法通信</li>
 * </ul>
 * 
 * <p><b>请求路径：</b>/api/alerts</p>
 * 
 * @author Campus Energy System
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
@Tag(name = "告警管理", description = "告警记录查询和处理接口")
public class AlertController {
    
    /** 告警服务层，处理告警相关的业务逻辑 */
    private final AlertService alertService;
    
    /**
     * 获取所有告警记录（分页）
     * 
     * <p>查询系统中所有告警记录，支持分页查询，默认每页20条，按触发时间倒序排列。
     * 包括已处理和未处理的告警。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/alerts?page=0&size=20&sort=triggerTime,desc</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>page：页码，查询参数，可选，默认0（从0开始）</li>
     *   <li>size：每页数量，查询参数，可选，默认20</li>
     *   <li>sort：排序字段，查询参数，可选，默认按triggerTime倒序</li>
     * </ul>
     * 
     * @param pageable 分页参数，Spring Data自动注入，支持page、size、sort等参数
     * @return 包含分页告警记录的Result对象
     */
    @GetMapping
    @Operation(summary = "获取所有告警记录（分页）", description = "查询系统中所有告警记录，支持分页查询")
    public Result<Page<AlertDTO>> getAllAlerts(
            @PageableDefault(size = 20, sort = "triggerTime") Pageable pageable) {
        return Result.success(alertService.getAllAlerts(pageable));
    }
    
    /**
     * 根据设备ID获取告警记录（分页）
     * 
     * <p>查询指定设备的所有告警记录，支持分页查询。适用于查看某个设备的告警历史。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/alerts/device/1?page=0&size=20</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>deviceId：设备ID，路径参数，必填</li>
     *   <li>page：页码，查询参数，可选，默认0</li>
     *   <li>size：每页数量，查询参数，可选，默认20</li>
     * </ul>
     * 
     * @param deviceId 设备ID，从URL路径中获取
     * @param pageable 分页参数，Spring Data自动注入
     * @return 包含该设备分页告警记录的Result对象
     */
    @GetMapping("/device/{deviceId}")
    @Operation(summary = "根据设备ID获取告警记录（分页）", description = "查询指定设备的所有告警记录，支持分页查询")
    public Result<Page<AlertDTO>> getAlertsByDeviceId(
            @Parameter(description = "设备ID", required = true, example = "1") @PathVariable Long deviceId,
            @PageableDefault(size = 20, sort = "triggerTime") Pageable pageable) {
        return Result.success(alertService.getAlertsByDeviceId(deviceId, pageable));
    }
    
    /**
     * 获取未处理的告警
     * 
     * <p>查询系统中所有状态为"未处理"的告警记录。适用于告警处理工作台、待处理任务列表等场景。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/alerts/unresolved</pre>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>告警处理工作台</li>
     *   <li>待处理任务提醒</li>
     *   <li>紧急告警监控</li>
     * </ul>
     * 
     * @return 包含所有未处理告警的Result对象
     */
    @GetMapping("/unresolved")
    @Operation(summary = "获取未处理的告警", description = "查询系统中所有状态为未处理的告警记录")
    public Result<List<AlertDTO>> getUnresolvedAlerts() {
        return Result.success(alertService.getUnresolvedAlerts());
    }
    
    /**
     * 获取最近的告警记录
     * 
     * <p>查询系统中最近触发的一批告警记录（默认返回最近10条）。
     * 适用于告警实时监控、最新告警展示等场景。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/alerts/recent</pre>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>实时告警监控</li>
     *   <li>最新告警展示</li>
     *   <li>告警通知</li>
     * </ul>
     * 
     * @return 包含最近告警记录的Result对象
     */
    @GetMapping("/recent")
    @Operation(summary = "获取最近的告警记录", description = "查询系统中最近触发的一批告警记录")
    public Result<List<AlertDTO>> getRecentAlerts() {
        return Result.success(alertService.getRecentAlerts());
    }
    
    /**
     * 根据时间范围获取告警
     * 
     * <p>查询指定时间段内的所有告警记录。适用于告警报表、历史告警分析等场景。
     * 时间格式为ISO 8601标准格式：yyyy-MM-ddTHH:mm:ss</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/alerts/range?startTime=2025-01-01T00:00:00&endTime=2025-01-01T23:59:59</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>startTime：开始时间，查询参数，必填，格式：yyyy-MM-ddTHH:mm:ss</li>
     *   <li>endTime：结束时间，查询参数，必填，格式：yyyy-MM-ddTHH:mm:ss</li>
     * </ul>
     * 
     * @param startTime 开始时间，从查询参数中获取，ISO 8601格式
     * @param endTime 结束时间，从查询参数中获取，ISO 8601格式
     * @return 包含时间范围内所有告警记录的Result对象
     */
    @GetMapping("/range")
    @Operation(summary = "根据时间范围获取告警", description = "查询指定时间段内的所有告警记录")
    public Result<List<AlertDTO>> getAlertsByTimeRange(
            @Parameter(description = "开始时间", required = true, example = "2025-01-01T00:00:00") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间", required = true, example = "2025-01-01T23:59:59") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return Result.success(alertService.getAlertsByTimeRange(startTime, endTime));
    }
    
    /**
     * 获取今日告警数量
     * 
     * <p>统计从今天00:00:00到当前时间触发的告警总数。适用于仪表盘统计、告警趋势分析等场景。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/alerts/count/today</pre>
     * 
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": 15,
     *   "timestamp": 1704067200000
     * }
     * </pre>
     * 
     * @return 包含今日告警数量的Result对象，data字段为Long类型
     */
    @GetMapping("/count/today")
    @Operation(summary = "获取今日告警数量", description = "统计从今天00:00:00到当前时间触发的告警总数")
    public Result<Long> getTodayAlertCount() {
        return Result.success(alertService.getTodayAlertCount());
    }
    
    /**
     * 获取未处理告警数量
     * 
     * <p>统计系统中所有状态为"未处理"的告警总数。适用于告警处理工作台、待处理任务提醒等场景。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/alerts/count/unresolved</pre>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>告警处理工作台待处理数量显示</li>
     *   <li>系统通知提醒</li>
     *   <li>告警处理效率统计</li>
     * </ul>
     * 
     * @return 包含未处理告警数量的Result对象，data字段为Long类型
     */
    @GetMapping("/count/unresolved")
    @Operation(summary = "获取未处理告警数量", description = "统计系统中所有状态为未处理的告警总数")
    public Result<Long> getUnresolvedAlertCount() {
        return Result.success(alertService.getUnresolvedAlertCount());
    }
    
    /**
     * 获取告警类型统计
     * 
     * <p>统计系统中各种告警类型的数量分布。返回一个Map，key为告警类型，value为该类型的数量。
     * 适用于告警分析、告警趋势展示等场景。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/alerts/stats/type</pre>
     * 
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": {
     *     "POWER_OVERLOAD": 10,
     *     "VOLTAGE_HIGH": 5,
     *     "DEVICE_OFFLINE": 3,
     *     "CURRENT_ABNORMAL": 2
     *   },
     *   "timestamp": 1704067200000
     * }
     * </pre>
     * 
     * @return 包含告警类型统计的Result对象，data字段为Map&lt;String, Long&gt;类型
     */
    @GetMapping("/stats/type")
    @Operation(summary = "获取告警类型统计", description = "统计系统中各种告警类型的数量分布")
    public Result<Map<String, Long>> getAlertTypeStats() {
        return Result.success(alertService.getAlertTypeStats());
    }
    
    /**
     * 处理告警
     * 
     * <p>将指定的告警标记为"已处理"状态，并可以添加处理备注。
     * 处理后的告警将不再出现在未处理告警列表中。</p>
     * 
     * <p><b>权限要求：</b>需要管理员（ADMIN）角色</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>POST /api/alerts/1/resolve?resolveNote=已检查设备，恢复正常</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>id：告警ID，路径参数，必填</li>
     *   <li>resolveNote：处理备注，查询参数，可选，用于记录处理情况</li>
     * </ul>
     * 
     * <p><b>处理效果：</b></p>
     * <ul>
     *   <li>告警状态更新为"已处理"</li>
     *   <li>记录处理时间和处理备注</li>
     *   <li>该告警不再出现在未处理告警列表中</li>
     * </ul>
     * 
     * <p><b>异常情况：</b></p>
     * <ul>
     *   <li>告警不存在：返回404错误</li>
     *   <li>告警已处理：返回400错误</li>
     * </ul>
     * 
     * @param id 告警ID，从URL路径中获取
     * @param resolveNote 处理备注，从查询参数中获取，可选
     * @return 包含更新后告警信息的Result对象
     */
    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "处理告警", description = "将指定的告警标记为已处理状态，需要管理员权限")
    public Result<AlertDTO> resolveAlert(
            @Parameter(description = "告警ID", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "处理备注", required = false) @RequestParam(required = false) String resolveNote) {
        return Result.success("告警已处理", alertService.resolveAlert(id, resolveNote));
    }
}

