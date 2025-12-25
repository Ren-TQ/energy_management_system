package com.energy.management.dto.response;

import com.energy.management.entity.Meter;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MeterResponse {
    private Long id;
    private String deviceName;
    private String serialNumber;
    private Meter.DeviceStatus status;
    private Double powerThreshold;
    private Long buildingId;
    private String buildingName;
    private String roomNumber;
    private LocalDateTime createdAt;
    private Boolean active;
    private EnergyDataResponse latestData;

    public static MeterResponse fromEntity(Meter meter) {
        MeterResponse response = new MeterResponse();
        response.setId(meter.getId());
        response.setDeviceName(meter.getDeviceName());
        response.setSerialNumber(meter.getSerialNumber());
        response.setStatus(meter.getStatus());
        response.setPowerThreshold(meter.getPowerThreshold());
        response.setBuildingId(meter.getBuilding().getId());
        response.setBuildingName(meter.getBuilding().getName());
        response.setRoomNumber(meter.getRoomNumber());
        response.setCreatedAt(meter.getCreatedAt());
        response.setActive(meter.getActive());
        return response;
    }
}