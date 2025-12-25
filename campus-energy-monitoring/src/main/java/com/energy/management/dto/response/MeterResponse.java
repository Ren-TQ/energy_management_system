package com.energy.management.dto.response;

import com.energy.management.entity.Meter;
import com.energy.management.enums.DeviceStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MeterResponse {
    private Long id;
    private String name;
    private String serialNumber;
    private DeviceStatus status;
    private Double ratedPower;
    private Long buildingId;
    private String buildingName;
    private String roomNumber;
    private String usageDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private EnergyDataResponse latestData;

    public static MeterResponse fromEntity(Meter meter) {
        MeterResponse response = new MeterResponse();
        response.setId(meter.getId());
        response.setName(meter.getName());
        response.setSerialNumber(meter.getSerialNumber());
        response.setStatus(meter.getStatus());
        response.setRatedPower(meter.getRatedPower());
        response.setBuildingId(meter.getBuilding().getId());
        response.setBuildingName(meter.getBuilding().getName());
        response.setRoomNumber(meter.getRoomNumber());
        response.setUsageDescription(meter.getUsageDescription());
        response.setCreatedAt(meter.getCreatedAt());
        response.setUpdatedAt(meter.getUpdatedAt());
        return response;
    }
}