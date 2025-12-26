package com.energy.management.dto.response;

import com.energy.management.entity.Building;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BuildingResponse {
    private Long id;
    private String name;
    private String locationCode;
    private Integer floorCount;
    private String category;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deviceCount;

    public static BuildingResponse fromEntity(Building building) {
        BuildingResponse response = new BuildingResponse();
        response.setId(building.getId());
        response.setName(building.getName());
        response.setLocationCode(building.getLocationCode());
        response.setFloorCount(building.getFloorCount());
        response.setCategory(building.getCategory());
        response.setDescription(building.getDescription());
        response.setCreatedAt(building.getCreatedAt());
        response.setUpdatedAt(building.getUpdatedAt());
        response.setDeviceCount(building.getDevices() != null ? building.getDevices().size() : 0);
        return response;
    }
}
