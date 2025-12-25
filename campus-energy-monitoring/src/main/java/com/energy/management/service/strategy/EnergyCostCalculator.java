package com.energy.management.service.strategy;

// Pattern: Strategy - 定义费用计算策略接口
public interface EnergyCostCalculator {
    double calculateCost(double energyConsumption);
}