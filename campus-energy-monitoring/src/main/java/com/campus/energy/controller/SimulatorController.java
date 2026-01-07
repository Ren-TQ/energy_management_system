package com.campus.energy.controller;

import com.campus.energy.dto.common.Result;
import com.campus.energy.simulator.EnergySimulatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟器控制器
 * 
 * <p>负责管理能耗数据模拟器的HTTP请求，提供模拟器的开启/关闭、状态查询、手动触发等功能。
 * 模拟器用于在开发和测试环境中自动生成模拟的能耗数据，方便系统测试和演示。</p>
 * 
 * <p><b>功能说明：</b></p>
 * <ul>
     *   <li>状态查询：查询模拟器的运行状态和已生成数据数量</li>
     *   <li>开关控制：开启或关闭模拟器（需要管理员权限）</li>
     *   <li>手动触发：立即为所有在线设备生成一次能耗数据（需要管理员权限）</li>
     * </ul>
 * 
 * <p><b>权限说明：</b></p>
 * <ul>
     *   <li>状态查询：所有已登录用户均可访问</li>
     *   <li>控制接口：需要管理员（ADMIN）角色</li>
     * </ul>
 * 
 * <p><b>模拟器说明：</b></p>
 * <ul>
     *   <li>模拟器开启后，会按照配置的间隔时间自动为所有在线设备生成能耗数据</li>
     *   <li>生成的数据包括电压、电流、功率、累计用电量等信息</li>
     *   <li>数据会模拟真实场景，包括正常数据和异常数据</li>
     *   <li>仅用于开发和测试环境，生产环境建议关闭</li>
     * </ul>
 * 
 * <p><b>请求路径：</b>/api/simulator</p>
 * 
 * @author Campus Energy System
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/simulator")
@RequiredArgsConstructor
@Tag(name = "模拟器管理", description = "能耗数据模拟器控制接口")
public class SimulatorController {
    
    /** 能耗数据模拟器服务层，处理模拟器相关的业务逻辑 */
    private final EnergySimulatorService simulatorService;
    
    /**
     * 获取模拟器状态
     * 
     * <p>查询模拟器的当前运行状态，包括是否启用、已生成数据数量等信息。
     * 适用于监控模拟器运行状态、查看数据生成情况等场景。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/simulator/status</pre>
     * 
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": {
     *     "enabled": true,
     *     "generatedDataCount": 1500
     *   },
     *   "timestamp": 1704067200000
     * }
     * </pre>
     * 
     * <p><b>响应字段说明：</b></p>
     * <ul>
     *   <li>enabled：模拟器是否启用，boolean类型</li>
     *   <li>generatedDataCount：已生成的数据总数，Long类型</li>
     * </ul>
     * 
     * @return 包含模拟器状态的Result对象
     */
    @GetMapping("/status")
    @Operation(summary = "获取模拟器状态", description = "查询模拟器的当前运行状态和已生成数据数量")
    public Result<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("enabled", simulatorService.isSimulatorEnabled());
        status.put("generatedDataCount", simulatorService.getGeneratedDataCount());
        return Result.success(status);
    }
    
    /**
     * 开启/关闭模拟器
     * 
     * <p>控制模拟器的开启或关闭状态。开启后，模拟器会按照配置的间隔时间自动生成数据；
     * 关闭后，模拟器停止生成数据。</p>
     * 
     * <p><b>权限要求：</b>需要管理员（ADMIN）角色</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>POST /api/simulator/toggle?enabled=true</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>enabled：是否启用，查询参数，必填，boolean类型
     *     <ul>
     *       <li>true：开启模拟器</li>
     *       <li>false：关闭模拟器</li>
     *     </ul>
     *   </li>
     * </ul>
     * 
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": {
     *     "enabled": true,
     *     "message": "模拟器已开启"
     *   },
     *   "timestamp": 1704067200000
     * }
     * </pre>
     * 
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>生产环境建议关闭模拟器</li>
     *   <li>开启模拟器会持续生成数据，注意数据库存储空间</li>
     * </ul>
     * 
     * @param enabled 是否启用模拟器，从查询参数中获取
     * @return 包含更新后状态的Result对象
     */
    @PostMapping("/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "开启/关闭模拟器", description = "控制模拟器的开启或关闭状态，需要管理员权限")
    public Result<Map<String, Object>> toggleSimulator(
            @Parameter(description = "是否启用", required = true, example = "true") @RequestParam boolean enabled) {
        simulatorService.setSimulatorEnabled(enabled);
        
        Map<String, Object> status = new HashMap<>();
        status.put("enabled", enabled);
        status.put("message", enabled ? "模拟器已开启" : "模拟器已关闭");
        
        return Result.success(status);
    }
    
    /**
     * 手动触发数据生成
     * 
     * <p>立即为所有在线设备生成一次能耗数据，不等待定时任务。
     * 适用于测试场景、立即查看数据效果等需求。</p>
     * 
     * <p><b>权限要求：</b>需要管理员（ADMIN）角色</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>POST /api/simulator/trigger</pre>
     * 
     * <p><b>功能说明：</b></p>
     * <ul>
     *   <li>立即为所有状态为"在线"的设备生成一次能耗数据</li>
     *   <li>生成的数据包括电压、电流、功率、累计用电量等</li>
     *   <li>数据会模拟真实场景，可能包含正常数据和异常数据</li>
     * </ul>
     * 
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": "已触发数据生成",
     *   "timestamp": 1704067200000
     * }
     * </pre>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>测试数据生成功能</li>
     *   <li>立即查看数据效果</li>
     *   <li>演示系统功能</li>
     * </ul>
     * 
     * @return 操作成功的Result对象
     */
    @PostMapping("/trigger")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "手动触发数据生成", description = "立即为所有在线设备生成一次能耗数据，需要管理员权限")
    public Result<String> triggerGeneration() {
        simulatorService.triggerManualGeneration();
        return Result.success("已触发数据生成");
    }
}

