package com.energy.management.pattern.factory;

import com.energy.management.entity.EnergyData;
import com.energy.management.entity.Meter;

public interface EnergyDataFactory {
    
    EnergyData createEnergyData(Meter device, Double lastTotalEnergy);
    
    String getFactoryName();
}

