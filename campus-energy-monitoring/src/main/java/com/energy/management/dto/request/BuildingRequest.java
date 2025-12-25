package com.energy.management.dto.request;

import com.energy.management.entity.Building;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class BuildingRequest {
    @NotBlank(message = "建筑名称不能为空")
    private String name;

    private String locationCode;

    private Integer floors;

    private Building.BuildingType type;
}