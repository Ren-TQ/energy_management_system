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
 * 设备信息DTO
 */
@Data
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

