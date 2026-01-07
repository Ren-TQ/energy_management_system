package com.campus.energy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 建筑信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "建筑信息")
public class BuildingDTO {
    
    @Schema(description = "建筑ID")
    private Long id;
    
    @NotBlank(message = "建筑名称不能为空")
    @Schema(description = "建筑名称", example = "力行楼")
    private String name;
    
    @NotBlank(message = "位置编号不能为空")
    @Schema(description = "位置编号", example = "BLD_001")
    private String locationCode;
    
    @NotNull(message = "楼层数不能为空")
    @Min(value = 1, message = "楼层数必须大于0")
    @Schema(description = "楼层数", example = "6")
    private Integer floorCount;
    
    @NotBlank(message = "建筑用途不能为空")
    @Schema(description = "建筑用途分类", example = "教学楼")
    private String category;
    
    @Schema(description = "建筑描述")
    private String description;
    
    @Schema(description = "设备数量")
    private Integer deviceCount;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}

