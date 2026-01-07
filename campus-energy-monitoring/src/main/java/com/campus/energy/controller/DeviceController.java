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
 * ============================================
 * MVC架构 - Controller层（控制器层）
 * ============================================
 * 
 * 架构说明：
 * 本类属于MVC架构中的Controller（C）层，负责：
 * 1. 接收HTTP请求（GET、POST、PUT、DELETE等）
 * 2. 参数验证和解析
 * 3. 调用Service层处理业务逻辑
 * 4. 返回JSON响应（View层）
 * 
 * MVC职责划分：
 * - Controller (C): 本类 - 处理HTTP请求和响应
 * - Model (M): DeviceService、DeviceRepository、Device实体
 * - View (V): Result<T> JSON响应格式
 * 
 * 设计原则：
 * - Controller层不包含业务逻辑，只负责请求转发
 * - 所有业务逻辑都在Service层处理
 * - 返回统一的Result<T>格式作为View
 * ============================================
 * 
 * 设备信息控制器
 * 
 * <p>负责处理智能电表设备相关的HTTP请求，提供设备的增删改查功能。
 * 本控制器遵循RESTful设计规范，所有接口返回统一的Result格式。</p>
 * 
 * <p><b>功能说明：</b></p>
 * <ul>
 *   <li>查询功能：支持查询所有设备、按ID查询、按建筑查询、查询在线设备等</li>
 *   <li>管理功能：支持创建、更新、删除设备（需要管理员权限）</li>
 *   <li>状态管理：支持更新设备状态（在线/离线/维护中/已停用）</li>
 * </ul>
 * 
 * <p><b>权限说明：</b></p>
 * <ul>
 *   <li>查询接口：所有已登录用户均可访问</li>
 *   <li>管理接口：仅管理员（ADMIN角色）可访问</li>
 * </ul>
 * 
 * <p><b>请求路径：</b>/api/devices</p>
 * 
 * @author Campus Energy System
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
@Tag(name = "设备管理", description = "智能电表设备的增删改查接口")
public class DeviceController {
    
    // ============================================
    // MVC架构 - Model层（模型层）
    // Service层：处理业务逻辑，属于Model的一部分
    // ============================================
    /** 设备服务层，处理设备相关的业务逻辑 */
    private final DeviceService deviceService;
    
    /**
     * 获取所有设备列表
     * 
     * <p>查询系统中所有已注册的智能电表设备，返回完整的设备信息列表。
     * 包括在线、离线、维护中等所有状态的设备。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/devices</pre>
     * 
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": [
     *     {
     *       "id": 1,
     *       "name": "宿舍智能电表-01",
     *       "serialNumber": "METER_QIU_301",
     *       "status": "ONLINE",
     *       "ratedPower": 1000.0,
     *       ...
     *     }
     *   ],
     *   "timestamp": 1704067200000
     * }
     * </pre>
     * 
     * @return 包含所有设备信息的Result对象，data字段为设备列表
     */
    /**
     * ============================================
     * MVC架构体现：
     * - Controller (C): 本方法接收GET请求
     * - Model (M): deviceService.getAllDevices() 处理业务逻辑
     * - View (V): Result<List<DeviceDTO>> 返回JSON响应
     * ============================================
     */
    @GetMapping
    @Operation(summary = "获取所有设备列表", description = "返回系统中所有智能电表设备的信息列表")
    public Result<List<DeviceDTO>> getAllDevices() {
        // MVC: Controller调用Model层（Service）处理业务
        // MVC: 返回Result作为View层（JSON响应）
        return Result.success(deviceService.getAllDevices());
    }
    
    /**
     * 根据设备ID获取设备详细信息
     * 
     * <p>通过设备唯一标识ID查询指定设备的完整信息，包括设备基本信息、
     * 所属建筑信息、额定功率、当前状态等。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/devices/1</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>id：设备ID，路径参数，必填，Long类型</li>
     * </ul>
     * 
     * <p><b>异常情况：</b></p>
     * <ul>
     *   <li>设备不存在：返回404错误</li>
     * </ul>
     * 
     * @param id 设备ID，从URL路径中获取
     * @return 包含设备详细信息的Result对象
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取设备信息", description = "通过设备ID查询设备的详细信息")
    public Result<DeviceDTO> getDeviceById(
            @Parameter(description = "设备ID", required = true, example = "1") @PathVariable Long id) {
        return Result.success(deviceService.getDeviceById(id));
    }
    
    /**
     * 根据建筑ID获取该建筑下的所有设备列表
     * 
     * <p>查询指定建筑内安装的所有智能电表设备。常用于按建筑维度查看设备分布情况。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/devices/building/1</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>buildingId：建筑ID，路径参数，必填，Long类型</li>
     * </ul>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>查看某栋建筑的所有电表设备</li>
     *   <li>建筑维度的设备统计和管理</li>
     * </ul>
     * 
     * @param buildingId 建筑ID，从URL路径中获取
     * @return 包含该建筑下所有设备信息的Result对象，data字段为设备列表
     */
    @GetMapping("/building/{buildingId}")
    @Operation(summary = "根据建筑ID获取设备列表", description = "查询指定建筑下的所有智能电表设备")
    public Result<List<DeviceDTO>> getDevicesByBuildingId(
            @Parameter(description = "建筑ID", required = true, example = "1") @PathVariable Long buildingId) {
        return Result.success(deviceService.getDevicesByBuildingId(buildingId));
    }
    
    /**
     * 获取所有在线设备列表
     * 
     * <p>查询当前所有状态为"在线"（ONLINE）的设备。在线设备表示设备正常通信，
     * 可以正常采集能耗数据。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/devices/online</pre>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>实时监控：查看当前可用的设备</li>
     *   <li>数据采集：筛选可正常采集数据的设备</li>
     *   <li>设备健康检查：统计在线设备数量</li>
     * </ul>
     * 
     * @return 包含所有在线设备信息的Result对象，data字段为在线设备列表
     */
    @GetMapping("/online")
    @Operation(summary = "获取所有在线设备", description = "查询当前所有状态为在线的智能电表设备")
    public Result<List<DeviceDTO>> getOnlineDevices() {
        return Result.success(deviceService.getOnlineDevices());
    }
    
    /**
     * 创建新的智能电表设备
     * 
     * <p>在系统中注册一个新的智能电表设备。需要提供设备的完整信息，
     * 包括设备名称、序列号、所属建筑、房间号、额定功率等。</p>
     * 
     * <p><b>权限要求：</b>需要管理员（ADMIN）角色</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>POST /api/devices
     * Content-Type: application/json
     * 
     * {
     *   "name": "宿舍智能电表-04",
     *   "serialNumber": "METER_QIU_304",
     *   "status": "ONLINE",
     *   "ratedPower": 1000.0,
     *   "buildingId": 1,
     *   "roomNumber": "304",
     *   "usageDescription": "严控违规电器(易跳闸)"
     * }</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>dto：设备信息DTO对象，请求体参数，必填，JSON格式</li>
     *   <li>serialNumber：设备序列号，必须唯一</li>
     *   <li>buildingId：所属建筑ID，必须存在</li>
     *   <li>roomNumber：房间号，同一建筑下必须唯一</li>
     * </ul>
     * 
     * <p><b>验证规则：</b></p>
     * <ul>
     *   <li>设备序列号不能重复</li>
     *   <li>同一建筑下的房间号不能重复</li>
     *   <li>额定功率必须大于0</li>
     * </ul>
     * 
     * @param dto 设备信息DTO，包含设备的完整信息，通过@Valid注解进行参数校验
     * @return 包含新创建设备信息的Result对象
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建设备", description = "在系统中注册新的智能电表设备，需要管理员权限")
    public Result<DeviceDTO> createDevice(@Valid @RequestBody DeviceDTO dto) {
        return Result.success("创建成功", deviceService.createDevice(dto));
    }
    
    /**
     * 更新设备信息
     * 
     * <p>更新指定设备的详细信息。可以修改设备名称、额定功率、用途描述等信息。
     * 注意：设备序列号、所属建筑、房间号等关键信息通常不允许修改。</p>
     * 
     * <p><b>权限要求：</b>需要管理员（ADMIN）角色</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>PUT /api/devices/1
     * Content-Type: application/json
     * 
     * {
     *   "name": "宿舍智能电表-01（已更新）",
     *   "ratedPower": 1200.0,
     *   "usageDescription": "更新后的描述"
     * }</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>id：设备ID，路径参数，必填</li>
     *   <li>dto：更新的设备信息DTO，请求体参数，必填</li>
     * </ul>
     * 
     * <p><b>异常情况：</b></p>
     * <ul>
     *   <li>设备不存在：返回404错误</li>
     *   <li>参数验证失败：返回400错误</li>
     * </ul>
     * 
     * @param id 设备ID，从URL路径中获取
     * @param dto 更新的设备信息DTO，通过@Valid注解进行参数校验
     * @return 包含更新后设备信息的Result对象
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新设备信息", description = "更新指定设备的详细信息，需要管理员权限")
    public Result<DeviceDTO> updateDevice(
            @Parameter(description = "设备ID", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody DeviceDTO dto) {
        return Result.success("更新成功", deviceService.updateDevice(id, dto));
    }
    
    /**
     * 更新设备状态
     * 
     * <p>修改设备的通信状态。设备状态包括：
     * <ul>
     *   <li>ONLINE：在线，设备正常通信</li>
     *   <li>OFFLINE：离线，设备无法通信</li>
     *   <li>MAINTENANCE：维护中，设备正在维护</li>
     *   <li>DECOMMISSIONED：已停用，设备已停用</li>
     * </ul>
     * </p>
     * 
     * <p><b>权限要求：</b>需要管理员（ADMIN）角色</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>PATCH /api/devices/1/status?status=OFFLINE</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>id：设备ID，路径参数，必填</li>
     *   <li>status：设备状态，查询参数，必填，枚举值：ONLINE/OFFLINE/MAINTENANCE/DECOMMISSIONED</li>
     * </ul>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>设备故障时设置为离线状态</li>
     *   <li>设备维护时设置为维护中状态</li>
     *   <li>设备恢复后设置为在线状态</li>
     * </ul>
     * 
     * @param id 设备ID，从URL路径中获取
     * @param status 设备状态，从查询参数中获取，枚举类型
     * @return 包含更新后设备信息的Result对象
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新设备状态", description = "修改设备的通信状态（在线/离线/维护中/已停用），需要管理员权限")
    public Result<DeviceDTO> updateDeviceStatus(
            @Parameter(description = "设备ID", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "设备状态", required = true, example = "ONLINE") @RequestParam DeviceStatus status) {
        return Result.success("状态更新成功", deviceService.updateDeviceStatus(id, status));
    }
    
    /**
     * 删除设备
     * 
     * <p>从系统中永久删除指定的智能电表设备。删除操作会级联删除该设备相关的
     * 能耗数据和告警记录，请谨慎操作。</p>
     * 
     * <p><b>权限要求：</b>需要管理员（ADMIN）角色</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>DELETE /api/devices/1</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>id：设备ID，路径参数，必填</li>
     * </ul>
     * 
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>删除操作不可恢复，请确认后再执行</li>
     *   <li>删除设备会同时删除该设备的所有历史能耗数据</li>
     *   <li>删除设备会同时删除该设备的所有告警记录</li>
     * </ul>
     * 
     * <p><b>异常情况：</b></p>
     * <ul>
     *   <li>设备不存在：返回404错误</li>
     *   <li>设备存在关联数据无法删除：返回400错误</li>
     * </ul>
     * 
     * @param id 设备ID，从URL路径中获取
     * @return 操作成功的Result对象，data字段为null
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除设备", description = "从系统中删除指定的智能电表设备（会级联删除相关数据），需要管理员权限")
    public Result<Void> deleteDevice(
            @Parameter(description = "设备ID", required = true, example = "1") @PathVariable Long id) {
        deviceService.deleteDevice(id);
        return Result.success("删除成功", null);
    }
}

