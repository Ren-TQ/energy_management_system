package com.campus.energy.pattern.factory;

import com.campus.energy.entity.Device;
import com.campus.energy.entity.EnergyData;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * ============================================
 * 设计模式：Factory Pattern（工厂模式）- 具体工厂实现
 * ============================================
 * 
 * 角色：ConcreteFactory（具体工厂）
 * 
 * 职责：生成异常能耗数据用于测试告警模块
 * 
 * 异常类型：
 * - 功率过载：功率飙升至额定功率的120%
 * - 电压过低：电压跌至180V
 * - 电压过高：电压升至260V
 */
@Component
public class AbnormalEnergyDataFactory implements EnergyDataFactory {
    
    private final Random random = new Random();
    
    // 异常类型枚举
    private enum AbnormalType {
        POWER_OVERLOAD,     // 功率过载
        VOLTAGE_LOW,        // 电压过低
        VOLTAGE_HIGH        // 电压过高
    }
    
    @Override
    public EnergyData createEnergyData(Device device, Double lastTotalEnergy) {
        LocalDateTime now = LocalDateTime.now();
        
        // 随机选择一种异常类型
        AbnormalType abnormalType = AbnormalType.values()[random.nextInt(AbnormalType.values().length)];
        
        double voltage;
        double power;
        
        switch (abnormalType) {
            case POWER_OVERLOAD:
                // 功率过载：电压正常，功率超过额定功率的120%
                voltage = 220.0 + random.nextGaussian() * 5.0;
                power = device.getRatedPower() * (1.2 + random.nextDouble() * 0.3); // 120%-150%
                break;
                
            case VOLTAGE_LOW:
                // 电压过低：电压跌至180V附近
                voltage = 170.0 + random.nextDouble() * 20.0; // 170V-190V
                power = device.getRatedPower() * (0.3 + random.nextDouble() * 0.3); // 30%-60%
                break;
                
            case VOLTAGE_HIGH:
                // 电压过高：电压升至250V以上
                voltage = 250.0 + random.nextDouble() * 20.0; // 250V-270V
                power = device.getRatedPower() * (0.3 + random.nextDouble() * 0.3); // 30%-60%
                break;
                
            default:
                voltage = 220.0;
                power = device.getRatedPower() * 0.5;
        }
        
        // 确保数值合理
        voltage = Math.round(voltage * 100.0) / 100.0;
        power = Math.round(power * 100.0) / 100.0;
        
        // 根据公式 I = P / U 计算电流
        double current = Math.round((power / voltage) * 100.0) / 100.0;
        
        // 计算累计用电量增量
        double energyIncrement = (power / 1000.0) * (5.0 / 3600.0);
        double totalEnergy = (lastTotalEnergy != null ? lastTotalEnergy : 0.0) + energyIncrement;
        totalEnergy = Math.round(totalEnergy * 1000.0) / 1000.0;
        
        return EnergyData.builder()
                .device(device)
                .voltage(voltage)
                .current(current)
                .power(power)
                .totalEnergy(totalEnergy)
                .collectTime(now)
                .isAbnormal(true)
                .build();
    }
    
    @Override
    public String getFactoryName() {
        return "异常能耗数据工厂";
    }
}

