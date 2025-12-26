package com.energy.management.dto;

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
@Schema(description = "能耗数据")
public class EnergyDataDTO {
    
    @Schema(description = "数据ID")
    private Long id;
    
    @Schema(description = "设备ID")
    private Long deviceId;
    
    @Schema(description = "设备名称")
    private String deviceName;
    
    @Schema(description = "设备序列号")
    private String deviceSerialNumber;
    
    @Schema(description = "当前电压(V)", example = "220.5")
    private Double voltage;
    
    @Schema(description = "当前电流(A)", example = "2.5")
    private Double current;
    
    @Schema(description = "当前实时功率(W)", example = "551.25")
    private Double power;
    
    @Schema(description = "累计用电量(kWh)", example = "125.5")
    private Double totalEnergy;
    
    @Schema(description = "数据是否异常")
    private Boolean isAbnormal;
    
    @Schema(description = "采集时间")
    private LocalDateTime collectTime;
}

