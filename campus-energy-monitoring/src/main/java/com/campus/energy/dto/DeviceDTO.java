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
 */
@Data
// ============================================
// 设计模式：Builder Pattern（建造者模式）
// ============================================
// 优势：
// 1. 链式调用：代码可读性强，对象创建过程清晰
// 2. 参数可选：可以只设置需要的字段，不需要的可以不设置
// 3. 避免构造函数参数过多：DTO通常有很多字段，构造函数会很复杂
// 4. 易于维护：新增字段时，只需在builder()链中添加，不影响现有代码

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

