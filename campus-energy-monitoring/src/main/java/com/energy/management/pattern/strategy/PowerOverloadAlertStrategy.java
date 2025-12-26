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
public class PowerOverloadAlertStrategy implements AlertStrategy {
    
    @Value("${alert.power.overload-ratio:1.2}")
    private double overloadRatio;
    
    @Override
    public Optional<Alert> checkAlert(Meter device, EnergyData energyData) {
        double threshold = device.getRatedPower() * overloadRatio;
        
        if (energyData.getPower() > threshold) {
            Alert alert = Alert.builder()
                    .device(device)
                    .alertType(AlertType.POWER_OVERLOAD)
                    .alertValue(energyData.getPower())
                    .thresholdValue(threshold)
                    .description(String.format(
                            "设备[%s]功率过载告警：当前功率 %.2fW，超过阈值 %.2fW（额定功率 %.2fW × %.0f%%）",
                            device.getName(),
                            energyData.getPower(),
                            threshold,
                            device.getRatedPower(),
                            overloadRatio * 100
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
        return "功率过载告警策略";
    }
}

