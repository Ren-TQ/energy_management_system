package com.energy.management.service.observer;

import com.energy.management.entity.Alert;
import com.energy.management.entity.Meter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

// Pattern: Observer - 事件发布者
@Component
public class AlertPublisher {
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public AlertPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishPowerOverloadAlert(Meter meter, Double powerValue) {
        AlertEvent event = new AlertEvent(meter, Alert.AlertType.POWER_OVERLOAD, powerValue);
        eventPublisher.publishEvent(event);
    }

    public void publishVoltageAbnormalAlert(Meter meter, Double voltageValue) {
        AlertEvent event = new AlertEvent(meter, Alert.AlertType.VOLTAGE_ABNORMAL, voltageValue);
        eventPublisher.publishEvent(event);
    }

    public void publishDeviceOfflineAlert(Meter meter) {
        AlertEvent event = new AlertEvent(meter, Alert.AlertType.DEVICE_OFFLINE, 0.0);
        eventPublisher.publishEvent(event);
    }
}