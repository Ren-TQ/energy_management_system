package com.campus.energy.dto;

import com.campus.energy.enums.DeviceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ============================================
 * MVC架构 - View层（视图层）- 数据传输对象
 * ============================================
 * 
 * 架构说明：
 * 本类属于MVC架构中的View（V）层，用于在Controller和前端之间传输数据
 * 
 * MVC职责划分：
 * - Controller (C): DeviceController - 接收请求，返回DTO
 * - Model (M): DeviceService、DeviceRepository、Device实体
 * - View (V): 本类（DTO）- 作为View层的数据载体
 *             Result<DeviceDTO> - JSON响应格式
 * 
 * 数据流转：
 * Entity（Model层）→ 转换为DTO（View层）→ 封装为Result → JSON响应
 * 
 * 说明：
 * 在RESTful API中，DTO作为View层的数据结构，用于：
 * 1. 隐藏Entity的内部细节
 * 2. 控制返回给前端的数据内容
 * 3. 支持数据验证
 * ============================================
 * 
 * ============================================
 * 设计模式：DTO Pattern（数据传输对象模式）
 * ============================================
 * 
 * 模式类型：数据传输层设计模式
 * 
 * 模式说明：
 * DTO（Data Transfer Object）用于在不同层之间传输数据，
 * 避免直接暴露实体类的内部结构，提供更安全、更灵活的数据传输。
 * 
 * 在此项目中的应用：
 * - 所有DTO类都在com.campus.energy.dto包下
 * - Entity -> DTO：隐藏数据库细节
 * - DTO -> Entity：验证和转换数据
 * 
 * 优势：
 * 1. 解耦实体类和API接口
 * 2. 控制数据传输的内容
 * 3. 支持数据验证（@Valid注解）
 * 4. 易于版本控制
 * 
 * 代码位置：
 * - 所有DTO类：com.campus.energy.dto包
 * - 使用位置：Controller层接收和返回DTO
 * ============================================
 * 
 * 设备信息DTO
 */
@Data
// ============================================
// 设计模式：Builder Pattern（建造者模式）
// 使用Lombok的@Builder注解自动生成建造者
// ============================================
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "智能电表设备信息")
public class DeviceDTO {
    
    @Schema(description = "设备ID")
    private Long id;
    
    @NotBlank(message = "设备名称不能为空")
    @Schema(description = "设备名称", example = "宿舍电表")
    private String name;
    
    @NotBlank(message = "设备序列号不能为空")
    @Schema(description = "唯一设备序列号", example = "METER_DORM_301")
    private String serialNumber;
    
    @Schema(description = "通讯状态")
    private DeviceStatus status;
    
    @Schema(description = "通讯状态描述")
    private String statusLabel;
    
    @NotNull(message = "额定功率不能为空")
    @Positive(message = "额定功率必须大于0")
    @Schema(description = "额定功率阈值(W)", example = "1000")
    private Double ratedPower;
    
    @NotNull(message = "所属建筑不能为空")
    @Schema(description = "所属建筑ID")
    private Long buildingId;
    
    @Schema(description = "所属建筑名称")
    private String buildingName;
    
    @NotBlank(message = "房间号不能为空")
    @Schema(description = "房间号", example = "301")
    private String roomNumber;
    
    @Schema(description = "设备用途描述")
    private String usageDescription;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}

