package com.energy.management.pattern.strategy;

import com.energy.management.entity.Alert;
import com.energy.management.entity.EnergyData;
import com.energy.management.entity.Meter;
import com.energy.management.enums.AlertType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class VoltageAbnormalAlertStrategy implements AlertStrategy {
    
    private static final double STANDARD_VOLTAGE = 220.0;
    
    @Value("${alert.voltage.min:198}")
    private double minVoltage;
    
    @Value("${alert.voltage.max:242}")
    private double maxVoltage;
    
    @Override
    public Optional<Alert> checkAlert(Meter device, EnergyData energyData) {
        double voltage = energyData.getVoltage();
        
        if (voltage < minVoltage) {
            Alert alert = Alert.builder()
                    .device(device)
                    .alertType(AlertType.VOLTAGE_LOW)
                    .alertValue(voltage)
                    .thresholdValue(minVoltage)
                    .description(String.format(
                            "设备[%s]电压过低告警：当前电压 %.2fV，低于正常范围下限 %.2fV（标准电压 %.0fV的90%%）",
                            device.getName(),
                            voltage,
                            minVoltage,
                            STANDARD_VOLTAGE
                    ))
                    .triggerTime(LocalDateTime.now())
                    .isResolved(false)
                    .build();
            
            return Optional.of(alert);
        }
        
        if (voltage > maxVoltage) {
            Alert alert = Alert.builder()
                    .device(device)
                    .alertType(AlertType.VOLTAGE_HIGH)
                    .alertValue(voltage)
                    .thresholdValue(maxVoltage)
                    .description(String.format(
                            "设备[%s]电压过高告警：当前电压 %.2fV，超过正常范围上限 %.2fV（标准电压 %.0fV的110%%）",
                            device.getName(),
                            voltage,
                            maxVoltage,
                            STANDARD_VOLTAGE
                    ))
                    .triggerTime(LocalDateTime.now())
                    .isResolved(false)
                    .build();
            
            return Optional.of(alert);
        }
        
        return Optional.empty();
    }
    
    @Override
    public String getStrategyName() {
        return "电压异常告警策略";
    }
}

