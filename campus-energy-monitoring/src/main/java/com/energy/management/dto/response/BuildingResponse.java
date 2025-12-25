package com.energy.management.dto.response;

import com.energy.management.entity.Building;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BuildingResponse {
    private Long id;
    private String name;
    private String locationCode;
    private Integer floors;
    private Building.BuildingType type;
    private LocalDateTime createdAt;
    private Integer meterCount;

    public static BuildingResponse fromEntity(Building building) {
        BuildingResponse response = new BuildingResponse();
        response.setId(building.getId());
        response.setName(building.getName());
        response.setLocationCode(building.getLocationCode());
        response.setFloors(building.getFloors());
        response.setType(building.getType());
        response.setCreatedAt(building.getCreatedAt());
        response.setMeterCount(building.getMeters() != null ? building.getMeters().size() : 0);
        return response;
    }
}