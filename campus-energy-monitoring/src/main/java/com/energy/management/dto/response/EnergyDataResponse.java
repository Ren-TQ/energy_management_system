package com.energy.management.dto.response;

import com.energy.management.entity.EnergyData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnergyDataResponse {
    private Long id;
    private Long meterId;
    private String meterName;
    private String serialNumber;
    private Double voltage;
    private Double current;
    private Double power;
    private Double energyConsumption;
    private LocalDateTime timestamp;

    public static EnergyDataResponse fromEntity(EnergyData data) {
        EnergyDataResponse response = new EnergyDataResponse();
        response.setId(data.getId());
        response.setMeterId(data.getDevice().getId());
        response.setMeterName(data.getDevice().getName());
        response.setSerialNumber(data.getDevice().getSerialNumber());
        response.setVoltage(data.getVoltage());
        response.setCurrent(data.getCurrent());
        response.setPower(data.getPower());
        response.setEnergyConsumption(data.getTotalEnergy());
        response.setTimestamp(data.getCollectTime());
        return response;
    }
}