package com.energy.management.service.strategy;

import org.springframework.stereotype.Component;

// Pattern: Strategy - 峰时费用计算策略实现
@Component("peakHourCalculator")
public class PeakHourCostCalculator implements EnergyCostCalculator {
    private static final double PEAK_RATE = 0.8; // 0.8元/度

    @Override
    public double calculateCost(double energyConsumption) {
        return energyConsumption * PEAK_RATE;
    }
}