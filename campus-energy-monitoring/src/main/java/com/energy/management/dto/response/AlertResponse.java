package com.energy.management.dto.response;

import com.energy.management.entity.Alert;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertResponse {
    private Long id;
    private Long meterId;
    private String meterName;
    private String serialNumber;
    private Alert.AlertType alertType;
    private Double alertValue;
    private String alertDetail;
    private LocalDateTime triggerTime;
    private Boolean isResolved;

    public static AlertResponse fromEntity(Alert alert) {
        AlertResponse response = new AlertResponse();
        response.setId(alert.getId());
        response.setMeterId(alert.getMeter().getId());
        response.setMeterName(alert.getMeter().getName());
        response.setSerialNumber(alert.getMeter().getSerialNumber());
        response.setAlertType(alert.getAlertType());
        response.setAlertValue(alert.getAlertValue());
        response.setAlertDetail(alert.getAlertDetail());
        response.setTriggerTime(alert.getTriggerTime());
        response.setIsResolved(alert.getIsResolved());
        return response;
    }
}