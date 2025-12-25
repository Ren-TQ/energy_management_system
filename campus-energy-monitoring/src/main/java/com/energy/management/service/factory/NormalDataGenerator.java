package com.energy.management.service.factory;

import com.energy.management.entity.EnergyData;
import com.energy.management.entity.Meter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

// Pattern: Factory - 正常数据生成器
@Component("normalGenerator")
public class NormalDataGenerator implements DataGenerator {

    @Override
    public EnergyData generateData(Meter meter) {
        EnergyData data = new EnergyData();
        data.setMeter(meter);

        // 根据时间生成不同的功率
        LocalTime now = LocalTime.now();
        boolean isDaytime = !now.isBefore(LocalTime.of(8, 0)) && now.isBefore(LocalTime.of(22, 0));

        // 生成电压：210V-235V正态分布
        double voltage = generateNormalVoltage();

        // 生成功率
        double power = generatePower(meter.getRatedPower(), isDaytime);

        // 根据物理公式 P = U×I 计算电流
        double current = power / voltage;

        data.setVoltage(Math.round(voltage * 100.0) / 100.0);
        data.setPower(Math.round(power * 100.0) / 100.0);
        data.setCurrent(Math.round(current * 100.0) / 100.0);

        // 模拟累计用电量（每次增加0.01-0.1 kWh）
        double increment = ThreadLocalRandom.current().nextDouble(0.01, 0.1);
        data.setEnergyConsumption(Math.round(increment * 100.0) / 100.0);

        data.setTimestamp(LocalDateTime.now());

        return data;
    }

    private double generateNormalVoltage() {
        // 正态分布，均值222.5，标准差3.5，范围210-235
        double voltage;
        do {
            voltage = ThreadLocalRandom.current().nextGaussian() * 3.5 + 222.5;
        } while (voltage < 210 || voltage > 235);
        return voltage;
    }

    private double generatePower(double threshold, boolean isDaytime) {
        if (isDaytime) {
            // 日间：0.2×阈值 到 0.9×阈值
            return ThreadLocalRandom.current().nextDouble(0.2 * threshold, 0.9 * threshold);
        } else {
            // 夜间：10W-100W
            return ThreadLocalRandom.current().nextDouble(10, 100);
        }
    }
}