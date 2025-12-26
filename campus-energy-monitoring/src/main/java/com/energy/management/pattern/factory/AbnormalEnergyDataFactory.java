package com.energy.management.pattern.factory;

import com.energy.management.entity.EnergyData;
import com.energy.management.entity.Meter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
public class AbnormalEnergyDataFactory implements EnergyDataFactory {
    
    private final Random random = new Random();
    
    private enum AbnormalType {
        POWER_OVERLOAD,
        VOLTAGE_LOW,
        VOLTAGE_HIGH
    }
    
    @Override
    public EnergyData createEnergyData(Meter device, Double lastTotalEnergy) {
        LocalDateTime now = LocalDateTime.now();
        
        AbnormalType abnormalType = AbnormalType.values()[random.nextInt(AbnormalType.values().length)];
        
        double voltage;
        double power;
        
        switch (abnormalType) {
            case POWER_OVERLOAD:
                voltage = 220.0 + random.nextGaussian() * 5.0;
                power = device.getRatedPower() * (1.2 + random.nextDouble() * 0.3);
                break;
                
            case VOLTAGE_LOW:
                voltage = 170.0 + random.nextDouble() * 20.0;
                power = device.getRatedPower() * (0.3 + random.nextDouble() * 0.3);
                break;
                
            case VOLTAGE_HIGH:
                voltage = 250.0 + random.nextDouble() * 20.0;
                power = device.getRatedPower() * (0.3 + random.nextDouble() * 0.3);
                break;
                
            default:
                voltage = 220.0;
                power = device.getRatedPower() * 0.5;
        }
        
        voltage = Math.round(voltage * 100.0) / 100.0;
        power = Math.round(power * 100.0) / 100.0;
        
        double current = Math.round((power / voltage) * 100.0) / 100.0;
        
        double energyIncrement = (power / 1000.0) * (5.0 / 3600.0);
        double totalEnergy = (lastTotalEnergy != null ? lastTotalEnergy : 0.0) + energyIncrement;
        totalEnergy = Math.round(totalEnergy * 1000.0) / 1000.0;
        
        return EnergyData.builder()
                .meter(device)
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

