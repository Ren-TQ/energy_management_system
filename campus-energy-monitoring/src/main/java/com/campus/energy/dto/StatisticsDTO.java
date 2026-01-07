package com.campus.energy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 统计数据DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "统计数据")
public class StatisticsDTO {
    
    @Schema(description = "建筑总数")
    private Long buildingCount;
    
    @Schema(description = "设备总数")
    private Long deviceCount;
    
    @Schema(description = "在线设备数")
    private Long onlineDeviceCount;
    
    @Schema(description = "离线设备数")
    private Long offlineDeviceCount;
    
    @Schema(description = "今日告警数")
    private Long todayAlertCount;
    
    @Schema(description = "未处理告警数")
    private Long unresolvedAlertCount;
    
    @Schema(description = "今日总用电量(kWh)")
    private Double todayTotalEnergy;
    
    @Schema(description = "本月总用电量(kWh)")
    private Double monthTotalEnergy;
    
    @Schema(description = "各建筑用电量统计")
    private List<BuildingEnergyStats> buildingEnergyStats;
    
    @Schema(description = "告警类型统计")
    private Map<String, Long> alertTypeStats;
    
    /**
     * 建筑用电量统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuildingEnergyStats {
        @Schema(description = "建筑ID")
        private Long buildingId;
        
        @Schema(description = "建筑名称")
        private String buildingName;
        
        @Schema(description = "设备数量")
        private Integer deviceCount;
        
        @Schema(description = "总用电量(kWh)")
        private Double totalEnergy;
    }
}

