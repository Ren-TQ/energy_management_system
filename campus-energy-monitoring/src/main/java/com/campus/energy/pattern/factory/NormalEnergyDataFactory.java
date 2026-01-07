package com.campus.energy.pattern.factory;

import com.campus.energy.entity.Device;
import com.campus.energy.entity.EnergyData;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

/**
 * ============================================
 * 设计模式：Factory Pattern（工厂模式）- 具体工厂实现
 * ============================================
 * 
 * 角色：ConcreteFactory（具体工厂）
 * 
 * 职责：生成符合物理规律的正常能耗数据
 * 
 * 数据模拟逻辑：
 * - 电压：在210V-235V之间正态分布波动
 * - 功率：日间(08:00-22:00)为额定功率的20%-90%，夜间为10W-100W
 * - 电流：根据公式 I = P / U 计算
 */
@Component
public class NormalEnergyDataFactory implements EnergyDataFactory {
    
    private static final double STANDARD_VOLTAGE = 220.0;
    private static final double VOLTAGE_DEVIATION = 7.5; // 正态分布标准差
    
    private final Random random = new Random();
    
    @Override
    public EnergyData createEnergyData(Device device, Double lastTotalEnergy) {
        LocalDateTime now = LocalDateTime.now();
        
        // 生成电压 (正态分布，均值220V，标准差7.5V)
        double voltage = generateNormalVoltage();
        
        // 根据时间段生成功率
        double power = generatePowerByTimeOfDay(device.getRatedPower(), now.toLocalTime());
        
        // 根据公式 I = P / U 计算电流
        double current = Math.round((power / voltage) * 100.0) / 100.0;
        
        // 计算累计用电量增量 (假设采集间隔5秒，转换为小时)
        double energyIncrement = (power / 1000.0) * (5.0 / 3600.0);
        double totalEnergy = (lastTotalEnergy != null ? lastTotalEnergy : 0.0) + energyIncrement;
        totalEnergy = Math.round(totalEnergy * 1000.0) / 1000.0;
        
        return EnergyData.builder()
                .device(device)
                .voltage(Math.round(voltage * 100.0) / 100.0)
                .current(current)
                .power(Math.round(power * 100.0) / 100.0)
                .totalEnergy(totalEnergy)
                .collectTime(now)
                .isAbnormal(false)
                .build();
    }
    
    /**
     * 生成正态分布的电压值
     */
    private double generateNormalVoltage() {
        double voltage = STANDARD_VOLTAGE + random.nextGaussian() * VOLTAGE_DEVIATION;
        // 确保电压在合理范围内 (210V - 235V)
        return Math.max(210.0, Math.min(235.0, voltage));
    }
    
    /**
     * 根据时间段生成功率
     * - 日间模式 (08:00 - 22:00)：额定功率的20%-90%
     * - 夜间模式 (22:00 - 08:00)：10W-100W待机功率
     */
    private double generatePowerByTimeOfDay(Double ratedPower, LocalTime currentTime) {
        boolean isDaytime = currentTime.isAfter(LocalTime.of(8, 0)) 
                         && currentTime.isBefore(LocalTime.of(22, 0));
        
        if (isDaytime) {
            // 日间：额定功率的20%-90%
            double minPower = ratedPower * 0.2;
            double maxPower = ratedPower * 0.9;
            return minPower + random.nextDouble() * (maxPower - minPower);
        } else {
            // 夜间：待机功率10W-100W
            return 10.0 + random.nextDouble() * 90.0;
        }
    }
    
    @Override
    public String getFactoryName() {
        return "正常能耗数据工厂";
    }
}

