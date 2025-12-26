package com.energy.management.pattern.factory;

import com.energy.management.entity.EnergyData;
import com.energy.management.entity.Meter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

@Component
public class NormalEnergyDataFactory implements EnergyDataFactory {
    
    private static final double STANDARD_VOLTAGE = 220.0;
    private static final double VOLTAGE_DEVIATION = 7.5;
    
    private final Random random = new Random();
    
    @Override
    public EnergyData createEnergyData(Meter device, Double lastTotalEnergy) {
        LocalDateTime now = LocalDateTime.now();
        
        double voltage = generateNormalVoltage();
        double power = generatePowerByTimeOfDay(device.getRatedPower(), now.toLocalTime());
        double current = Math.round((power / voltage) * 100.0) / 100.0;
        
        double energyIncrement = (power / 1000.0) * (5.0 / 3600.0);
        double totalEnergy = (lastTotalEnergy != null ? lastTotalEnergy : 0.0) + energyIncrement;
        totalEnergy = Math.round(totalEnergy * 1000.0) / 1000.0;
        
        return EnergyData.builder()
                .meter(device)
                .voltage(Math.round(voltage * 100.0) / 100.0)
                .current(current)
                .power(Math.round(power * 100.0) / 100.0)
                .totalEnergy(totalEnergy)
                .collectTime(now)
                .isAbnormal(false)
                .build();
    }
    
    private double generateNormalVoltage() {
        double voltage = STANDARD_VOLTAGE + random.nextGaussian() * VOLTAGE_DEVIATION;
        return Math.max(210.0, Math.min(235.0, voltage));
    }
    
    private double generatePowerByTimeOfDay(Double ratedPower, LocalTime currentTime) {
        boolean isDaytime = currentTime.isAfter(LocalTime.of(8, 0)) 
                         && currentTime.isBefore(LocalTime.of(22, 0));
        
        if (isDaytime) {
            double minPower = ratedPower * 0.2;
            double maxPower = ratedPower * 0.9;
            return minPower + random.nextDouble() * (maxPower - minPower);
        } else {
            return 10.0 + random.nextDouble() * 90.0;
        }
    }
    
    @Override
    public String getFactoryName() {
        return "正常能耗数据工厂";
    }
}

