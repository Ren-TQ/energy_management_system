package com.energy.management.service.strategy;

import org.springframework.stereotype.Component;

// Pattern: Strategy - 谷时费用计算策略实现
@Component("offPeakCalculator")
public class OffPeakCostCalculator implements EnergyCostCalculator {
    private static final double OFF_PEAK_RATE = 0.4; // 0.4元/度

    @Override
    public double calculateCost(double energyConsumption) {
        return energyConsumption * OFF_PEAK_RATE;
    }
}