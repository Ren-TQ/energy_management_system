package com.campus.energy.pattern.strategy;

import com.campus.energy.entity.Alert;
import com.campus.energy.entity.Device;
import com.campus.energy.entity.EnergyData;
import com.campus.energy.enums.AlertType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * ============================================
 * 设计模式：Strategy Pattern（策略模式）- 具体策略实现
 * ============================================
 * 
 * 角色：ConcreteStrategy（具体策略）
 * 
 * 职责：当实时功率超过设备额定功率的一定比例时触发告警
 * 
 * 判断逻辑：
 * - 阈值 = 额定功率 × 过载比例（默认120%）
 * - 当实时功率 > 阈值时，触发功率过载告警
 * ============================================
 */
@Component
public class PowerOverloadAlertStrategy implements AlertStrategy {
    
    @Value("${alert.power.overload-ratio:1.2}")
    private double overloadRatio;
    
    @Override
    public Optional<Alert> checkAlert(Device device, EnergyData energyData) {
        // 计算告警阈值：额定功率 × 过载比例
        double threshold = device.getRatedPower() * overloadRatio;
        
        // 检查是否超过阈值
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

