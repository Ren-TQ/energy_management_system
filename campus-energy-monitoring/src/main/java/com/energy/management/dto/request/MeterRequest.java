package com.energy.management.dto.request;

import com.energy.management.enums.DeviceStatus;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class MeterRequest {
    @NotBlank(message = "设备名称不能为空")
    private String name;

    @NotBlank(message = "设备序列号不能为空")
    private String serialNumber;

    @NotNull(message = "建筑ID不能为空")
    private Long buildingId;

    @NotBlank(message = "房间号不能为空")
    private String roomNumber;

    @NotNull(message = "功率阈值不能为空")
    private Double ratedPower;
    
    private DeviceStatus status = DeviceStatus.ONLINE;
    
    private String usageDescription;
}