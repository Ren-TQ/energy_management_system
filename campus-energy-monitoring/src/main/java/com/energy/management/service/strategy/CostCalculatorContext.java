package com.energy.management.service.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

// Pattern: Strategy - 策略上下文，根据条件选择具体策略
@Service
public class CostCalculatorContext {
    @Autowired
    private Map<String, EnergyCostCalculator> calculatorMap;

    public double calculate(String period, double energy) {
        String beanName = period.toLowerCase() + "Calculator";
        EnergyCostCalculator calculator = calculatorMap.get(beanName);

        if (calculator == null) {
            throw new IllegalArgumentException("不支持的电费计算时段: " + period);
        }

        return calculator.calculateCost(energy);
    }

    // 判断当前时间是否为峰时
    public boolean isPeakHour() {
        // 模拟：08:00-22:00为峰时
        int hour = java.time.LocalTime.now().getHour();
        return hour >= 8 && hour < 22;
    }
}