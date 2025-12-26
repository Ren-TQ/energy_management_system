package com.energy.management.pattern.strategy;

import com.energy.management.entity.Alert;
import com.energy.management.entity.EnergyData;
import com.energy.management.entity.Meter;

import java.util.Optional;

public interface AlertStrategy {
    
    Optional<Alert> checkAlert(Meter device, EnergyData energyData);
    
    String getStrategyName();
}

