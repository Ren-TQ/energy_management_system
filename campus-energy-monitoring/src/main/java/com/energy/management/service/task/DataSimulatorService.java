package com.energy.management.service.task;

import com.energy.management.entity.EnergyData;
import com.energy.management.entity.Meter;
import com.energy.management.service.factory.DataGeneratorFactory;
import com.energy.management.service.observer.AlertPublisher;
import com.energy.management.repository.EnergyDataRepository;
import com.energy.management.repository.MeterRepository;
import com.energy.management.service.EnergyDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class DataSimulatorService {

    @Autowired
    private MeterRepository meterRepository;

    @Autowired
    private EnergyDataRepository energyDataRepository;

    @Autowired
    private DataGeneratorFactory generatorFactory;

    @Autowired
    private AlertPublisher alertPublisher;

    @Autowired
    private EnergyDataService energyDataService;

    private final AtomicInteger dataCount = new AtomicInteger(0);
    private final AtomicInteger totalCount = new AtomicInteger(0);

    /**
     * Pattern: Scheduled Task - 定时生成模拟数据
     * 每5秒执行一次，生成所有活跃设备的能耗数据
     */
    @Scheduled(fixedRate = 5000)  // 5秒
    @Transactional
    public void generateSimulatedData() {
        List<Meter> activeMeters = meterRepository.findByActiveTrue();

        if (activeMeters == null || activeMeters.isEmpty()) {
            log.warn("没有活跃的设备，跳过数据生成");
            return;
        }

        // 精确控制只生成10台设备数据
        int deviceLimit = Math.min(activeMeters.size(), 10);
        log.info("开始生成模拟数据，设备数量: {}", deviceLimit);

        for (int i = 0; i < deviceLimit; i++) {
            try {
                generateMeterData(activeMeters.get(i));
            } catch (Exception e) {
                log.error("为设备 {} 生成数据失败: {}", activeMeters.get(i).getSerialNumber(), e.getMessage());
            }
        }

        int currentCount = dataCount.incrementAndGet();
        log.info("第 {} 轮数据生成完成", currentCount);
    }

    private void generateMeterData(Meter meter) {
        // 每生成30条正常数据后生成一条异常数据
        int currentTotal = totalCount.incrementAndGet();
        boolean shouldGenerateAnomaly = currentTotal > 0 && currentTotal % 30 == 0;

        String generatorType = shouldGenerateAnomaly ? "anomaly" : "normal";
        // Pattern: Factory - 使用工厂模式根据类型生成不同的数据
        EnergyData data = generatorFactory.getGenerator(generatorType).generateData(meter);

        // 保存数据
        energyDataRepository.save(data);

        // Pattern: Observer - 使用观察者模式检查并触发告警
        checkAndTriggerAlert(meter, data);

        log.debug("生成数据 - 设备: {}, 电压: {}V, 电流: {}A, 功率: {}W, 类型: {}",
                meter.getSerialNumber(),
                data.getVoltage(),
                data.getCurrent(),
                data.getPower(),
                generatorType);
    }

    private int getAnomalyInterval() {
        // 随机间隔20-50次
        return 20 + (int)(Math.random() * 31);
    }

    private void checkAndTriggerAlert(Meter meter, EnergyData data) {
        // 检查功率超限
        if (meter.getRatedPower() != null && data.getPower() > meter.getRatedPower()) {
            // Pattern: Observer - 发布功率超限告警事件
            alertPublisher.publishPowerOverloadAlert(meter, data.getPower());
        }

        // 检查电压异常（±10%）
        if (data.getVoltage() < 198 || data.getVoltage() > 242) {
            // Pattern: Observer - 发布电压异常告警事件
            alertPublisher.publishVoltageAbnormalAlert(meter, data.getVoltage());
        }

        // 检查设备状态（这里简化处理，实际需要更复杂的状态监控）
        // 可以添加设备离线的检测逻辑
    }

    /**
     * 每小时执行一次，清理过期数据（保留30天）
     */
    @Scheduled(cron = "0 0 * * * ?")  // 每小时整点
    @Transactional
    public void cleanupOldData() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);

        // 这里需要实现数据清理逻辑
        // 在实际项目中，可以使用@Modifying查询或分区表
        log.info("数据清理任务执行，清理30天前的数据");
    }
}