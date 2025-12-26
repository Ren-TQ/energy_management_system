package com.energy.management.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class BuildingRequest {
    @NotBlank(message = "建筑名称不能为空")
    private String name;

    private String locationCode;

    private Integer floorCount;

    private String category;

    private String description;
}
