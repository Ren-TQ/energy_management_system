package com.energy.management.dto;

import com.energy.management.enums.AlertType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "告警记录")
public class AlertDTO {
    
    @Schema(description = "告警ID")
    private Long id;
    
    @Schema(description = "设备ID")
    private Long deviceId;
    
    @Schema(description = "设备名称")
    private String deviceName;
    
    @Schema(description = "设备序列号")
    private String deviceSerialNumber;
    
    @Schema(description = "所属建筑名称")
    private String buildingName;
    
    @Schema(description = "房间号")
    private String roomNumber;
    
    @Schema(description = "告警类型")
    private AlertType alertType;
    
    @Schema(description = "告警类型描述")
    private String alertTypeLabel;
    
    @Schema(description = "告警数值")
    private Double alertValue;
    
    @Schema(description = "阈值")
    private Double thresholdValue;
    
    @Schema(description = "告警详情")
    private String description;
    
    @Schema(description = "是否已处理")
    private Boolean isResolved;
    
    @Schema(description = "处理时间")
    private LocalDateTime resolvedAt;
    
    @Schema(description = "处理备注")
    private String resolveNote;
    
    @Schema(description = "触发时间")
    private LocalDateTime triggerTime;
}

