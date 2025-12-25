package com.energy.management.service.factory;

import com.energy.management.entity.EnergyData;
import com.energy.management.entity.Meter;

// Pattern: Factory - 数据生成器工厂接口
public interface DataGenerator {
    EnergyData generateData(Meter meter);
}