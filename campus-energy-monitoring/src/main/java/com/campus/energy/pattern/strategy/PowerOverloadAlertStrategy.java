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
 * 职责：检测设备功率过载情况，当实时功率超过设备额定功率的一定比例时触发告警

 * 判断逻辑：
 * 1. 计算告警阈值 = 设备额定功率 × 过载比例（默认120%，可配置）
 * 2. 比较：实时功率 > 阈值
 * 3. 如果超过阈值，构建并返回功率过载告警对象
 * 4. 如果未超过，返回Optional.empty()

 * ============================================
 */
@Component  // Spring自动管理，自动注入到AlertService的alertStrategies列表
public class PowerOverloadAlertStrategy implements AlertStrategy {
    
    /**
     * 功率过载比例
     */
    @Value("${alert.power.overload-ratio:1.2}")
    private double overloadRatio;
    
    /**
     * 策略模式核心方法：检查功率是否过载
     */
    @Override
    public Optional<Alert> checkAlert(Device device, EnergyData energyData) {
        // ============================================
        // 步骤1：计算告警阈值
        // 阈值 = 设备额定功率 × 过载比例
        // 例如：1000W × 1.2 = 1200W
        // ============================================
        double threshold = device.getRatedPower() * overloadRatio;
        
        // ============================================
        // 步骤2：判断是否超过阈值
        // 策略模式：封装判断逻辑，独立于调用方
        // ============================================
        if (energyData.getPower() > threshold) {
            // ============================================
            // 步骤3：构建告警对象
            // 使用Builder模式创建告警对象
            // ============================================
            Alert alert = Alert.builder()
                    .device(device)  // 关联设备
                    .alertType(AlertType.POWER_OVERLOAD)  // 告警类型：功率过载
                    .alertValue(energyData.getPower())  // 当前实时功率值
                    .thresholdValue(threshold)  // 告警阈值
                    .description(String.format(
                            // 告警描述：包含详细信息，便于排查问题
                            "设备[%s]功率过载告警：当前功率 %.2fW，超过阈值 %.2fW（额定功率 %.2fW × %.0f%%）",
                            device.getName(),           // 设备名称
                            energyData.getPower(),      // 当前功率
                            threshold,                  // 阈值
                            device.getRatedPower(),     // 额定功率
                            overloadRatio * 100         // 过载比例（转换为百分比）
                    ))
                    .triggerTime(LocalDateTime.now())  // 触发时间
                    .isResolved(false)  // 初始状态：未处理
                    .build();
            
            // ============================================
            // 步骤4：返回告警对象
            // 策略模式：返回Optional，由调用方决定如何处理
            // ============================================
            return Optional.of(alert);
        }
        
        // ============================================
        // 功率正常，不触发告警
        // 策略模式：返回空，表示此策略未检测到异常
        // ============================================
        return Optional.empty();
    }
    
    /**
     * 获取策略名称
     * 
     * 用于日志记录和调试，标识当前策略
     * 
     * @return "功率过载告警策略"
     */
    @Override
    public String getStrategyName() {
        return "功率过载告警策略";
    }
}

