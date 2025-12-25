package com.energy.management.service.factory;

import com.energy.management.entity.EnergyData;
import com.energy.management.entity.Meter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

// Pattern: Factory - 异常数据生成器
@Component("anomalyGenerator")
public class AnomalyDataGenerator implements DataGenerator {

    @Override
    public EnergyData generateData(Meter meter) {
        EnergyData data = new EnergyData();
        data.setMeter(meter);

        // 随机选择一种异常类型
        int anomalyType = ThreadLocalRandom.current().nextInt(3);

        switch (anomalyType) {
            case 0: // 功率过载
                data.setVoltage(220.0);
                data.setPower(meter.getRatedPower() * 1.2); // 超过阈值20%
                data.setAlertDetail("功率过载异常");
                break;

            case 1: // 电压过高
                data.setVoltage(260.0); // 明显高于正常范围
                data.setPower(meter.getRatedPower() * 0.6);
                data.setAlertDetail("电压过高异常");
                break;

            case 2: // 电压过低
                data.setVoltage(180.0); // 明显低于正常范围
                data.setPower(meter.getRatedPower() * 0.4);
                data.setAlertDetail("电压过低异常");
                break;
        }

        // 根据物理公式计算电流
        double current = data.getPower() / data.getVoltage();
        data.setCurrent(Math.round(current * 100.0) / 100.0);

        // 异常数据的累计用电量增加更多
        data.setEnergyConsumption(Math.round(ThreadLocalRandom.current().nextDouble(0.1, 0.5) * 100.0) / 100.0);

        data.setTimestamp(LocalDateTime.now());

        return data;
    }
}