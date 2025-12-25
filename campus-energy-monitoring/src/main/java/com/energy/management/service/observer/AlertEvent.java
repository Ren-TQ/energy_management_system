package com.energy.management.service.observer;

import com.energy.management.entity.Alert;
import com.energy.management.entity.Meter;

// Pattern: Observer - 告警事件类
public class AlertEvent {
    private final Meter meter;
    private final Alert.AlertType alertType;
    private final Double value;
    private final String detail;

    public AlertEvent(Meter meter, Alert.AlertType alertType, Double value) {
        this.meter = meter;
        this.alertType = alertType;
        this.value = value;
        this.detail = generateAlertDetail(alertType, value);
    }

    private String generateAlertDetail(Alert.AlertType type, Double value) {
        switch (type) {
            case POWER_OVERLOAD:
                return String.format("设备功率超限: %.2fW (阈值: %.2fW)", value, meter.getPowerThreshold());
            case VOLTAGE_ABNORMAL:
                return String.format("电压异常: %.2fV (正常范围: 198V-242V)", value);
            case DEVICE_OFFLINE:
                return "设备离线超过30分钟";
            default:
                return "未知告警";
        }
    }

    // Getters
    public Meter getMeter() { return meter; }
    public Alert.AlertType getAlertType() { return alertType; }
    public Double getValue() { return value; }
    public String getDetail() { return detail; }
}