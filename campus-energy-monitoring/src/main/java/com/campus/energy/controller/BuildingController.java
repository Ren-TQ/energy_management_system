package com.campus.energy.controller;

import com.campus.energy.dto.BuildingDTO;
import com.campus.energy.dto.common.Result;
import com.campus.energy.service.BuildingService;
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
 * - Model (M): BuildingService、BuildingRepository、Building实体
 * - View (V): Result<T> JSON响应格式
 * ============================================
 * 
 * 建筑信息控制器
 * 
 * <p>负责处理校园建筑相关的HTTP请求，提供建筑信息的增删改查功能。
 * 建筑是设备的上层组织单位，每个设备必须属于某个建筑。</p>
 * 
 * <p><b>功能说明：</b></p>
 * <ul>
 *   <li>查询功能：支持查询所有建筑、按ID查询、查询建筑分类等</li>
 *   <li>管理功能：支持创建、更新、删除建筑（需要管理员权限）</li>
 * </ul>
 * 
 * <p><b>权限说明：</b></p>
 * <ul>
 *   <li>查询接口：所有已登录用户均可访问</li>
 *   <li>管理接口：仅管理员（ADMIN角色）可访问</li>
 * </ul>
 * 
 * <p><b>请求路径：</b>/api/buildings</p>
 * 
 * @author Campus Energy System
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/buildings")
@RequiredArgsConstructor
@Tag(name = "建筑管理", description = "建筑信息的增删改查接口")
public class BuildingController {
    
    /** 建筑服务层，处理建筑相关的业务逻辑 */
    private final BuildingService buildingService;
    
    /**
     * 获取所有建筑列表
     * 
     * <p>查询系统中所有已注册的校园建筑，返回完整的建筑信息列表。
     * 包括教学楼、宿舍楼、图书馆等所有类型的建筑。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/buildings</pre>
     * 
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": [
     *     {
     *       "id": 1,
     *       "name": "楸苑宿舍三号楼",
     *       "locationCode": "BLD_QIU_003",
     *       "floorCount": 7,
     *       "category": "宿舍楼",
     *       ...
     *     }
     *   ],
     *   "timestamp": 1704067200000
     * }
     * </pre>
     * 
     * @return 包含所有建筑信息的Result对象，data字段为建筑列表
     */
    @GetMapping
    @Operation(summary = "获取所有建筑列表", description = "返回系统中所有校园建筑的信息列表")
    public Result<List<BuildingDTO>> getAllBuildings() {
        return Result.success(buildingService.getAllBuildings());
    }
    
    /**
     * 根据建筑ID获取建筑详细信息
     * 
     * <p>通过建筑唯一标识ID查询指定建筑的完整信息，包括建筑名称、位置编号、
     * 楼层数、分类、描述等，以及该建筑下的所有设备信息。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/buildings/1</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>id：建筑ID，路径参数，必填，Long类型</li>
     * </ul>
     * 
     * <p><b>异常情况：</b></p>
     * <ul>
     *   <li>建筑不存在：返回404错误</li>
     * </ul>
     * 
     * @param id 建筑ID，从URL路径中获取
     * @return 包含建筑详细信息的Result对象
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取建筑信息", description = "通过建筑ID查询建筑的详细信息")
    public Result<BuildingDTO> getBuildingById(
            @Parameter(description = "建筑ID", required = true, example = "1") @PathVariable Long id) {
        return Result.success(buildingService.getBuildingById(id));
    }
    
    /**
     * 获取所有建筑分类列表
     * 
     * <p>查询系统中所有不同的建筑分类，如：教学楼、宿舍楼、图书馆、办公楼等。
     * 用于前端下拉选择框或分类筛选功能。</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>GET /api/buildings/categories</pre>
     * 
     * <p><b>响应示例：</b></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": ["教学楼", "宿舍楼", "图书馆", "办公楼"],
     *   "timestamp": 1704067200000
     * }
     * </pre>
     * 
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>前端分类筛选下拉框</li>
     *   <li>按分类统计建筑数量</li>
     *   <li>建筑分类管理</li>
     * </ul>
     * 
     * @return 包含所有建筑分类的Result对象，data字段为分类字符串列表
     */
    @GetMapping("/categories")
    @Operation(summary = "获取所有建筑分类", description = "查询系统中所有不同的建筑分类（如：教学楼、宿舍楼等）")
    public Result<List<String>> getAllCategories() {
        return Result.success(buildingService.getAllCategories());
    }
    
    /**
     * 创建新的建筑
     * 
     * <p>在系统中注册一个新的校园建筑。需要提供建筑的完整信息，
     * 包括建筑名称、位置编号、楼层数、分类、描述等。</p>
     * 
     * <p><b>权限要求：</b>需要管理员（ADMIN）角色</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>POST /api/buildings
     * Content-Type: application/json
     * 
     * {
     *   "name": "实验楼",
     *   "locationCode": "BLD_LAB_001",
     *   "floorCount": 5,
     *   "category": "教学楼",
     *   "description": "实验教学专用楼"
     * }</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>dto：建筑信息DTO对象，请求体参数，必填，JSON格式</li>
     *   <li>locationCode：位置编号，必须唯一</li>
     *   <li>name：建筑名称，必填</li>
     *   <li>category：建筑分类，必填</li>
     * </ul>
     * 
     * <p><b>验证规则：</b></p>
     * <ul>
     *   <li>位置编号不能重复</li>
     *   <li>楼层数必须大于0</li>
     *   <li>建筑名称不能为空</li>
     * </ul>
     * 
     * @param dto 建筑信息DTO，包含建筑的完整信息，通过@Valid注解进行参数校验
     * @return 包含新创建建筑信息的Result对象
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建建筑", description = "在系统中注册新的校园建筑，需要管理员权限")
    public Result<BuildingDTO> createBuilding(@Valid @RequestBody BuildingDTO dto) {
        return Result.success("创建成功", buildingService.createBuilding(dto));
    }
    
    /**
     * 更新建筑信息
     * 
     * <p>更新指定建筑的详细信息。可以修改建筑名称、楼层数、分类、描述等信息。
     * 注意：位置编号等关键信息通常不允许修改。</p>
     * 
     * <p><b>权限要求：</b>需要管理员（ADMIN）角色</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>PUT /api/buildings/1
     * Content-Type: application/json
     * 
     * {
     *   "name": "楸苑宿舍三号楼（已更名）",
     *   "floorCount": 8,
     *   "description": "更新后的描述"
     * }</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>id：建筑ID，路径参数，必填</li>
     *   <li>dto：更新的建筑信息DTO，请求体参数，必填</li>
     * </ul>
     * 
     * <p><b>异常情况：</b></p>
     * <ul>
     *   <li>建筑不存在：返回404错误</li>
     *   <li>参数验证失败：返回400错误</li>
     * </ul>
     * 
     * @param id 建筑ID，从URL路径中获取
     * @param dto 更新的建筑信息DTO，通过@Valid注解进行参数校验
     * @return 包含更新后建筑信息的Result对象
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新建筑信息", description = "更新指定建筑的详细信息，需要管理员权限")
    public Result<BuildingDTO> updateBuilding(
            @Parameter(description = "建筑ID", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody BuildingDTO dto) {
        return Result.success("更新成功", buildingService.updateBuilding(id, dto));
    }
    
    /**
     * 删除建筑
     * 
     * <p>从系统中永久删除指定的建筑。删除操作会检查该建筑下是否还有设备，
     * 如果存在设备，则不允许删除，需要先删除或迁移所有设备。</p>
     * 
     * <p><b>权限要求：</b>需要管理员（ADMIN）角色</p>
     * 
     * <p><b>请求示例：</b></p>
     * <pre>DELETE /api/buildings/1</pre>
     * 
     * <p><b>参数说明：</b></p>
     * <ul>
     *   <li>id：建筑ID，路径参数，必填</li>
     * </ul>
     * 
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>删除操作不可恢复，请确认后再执行</li>
     *   <li>如果建筑下还有设备，删除操作会失败</li>
     *   <li>需要先删除或迁移该建筑下的所有设备后才能删除建筑</li>
     * </ul>
     * 
     * <p><b>异常情况：</b></p>
     * <ul>
     *   <li>建筑不存在：返回404错误</li>
     *   <li>建筑下还有设备：返回400错误，提示需要先处理设备</li>
     * </ul>
     * 
     * @param id 建筑ID，从URL路径中获取
     * @return 操作成功的Result对象，data字段为null
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除建筑", description = "从系统中删除指定的建筑（需确保建筑下无设备），需要管理员权限")
    public Result<Void> deleteBuilding(
            @Parameter(description = "建筑ID", required = true, example = "1") @PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return Result.success("删除成功", null);
    }
}

