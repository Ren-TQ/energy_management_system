package com.energy.management.service.task;

import com.energy.management.entity.EnergyData;
import com.energy.management.entity.Meter;
import com.energy.management.enums.DeviceStatus;
import com.energy.management.pattern.factory.AbnormalEnergyDataFactory;
import com.energy.management.pattern.factory.NormalEnergyDataFactory;
import com.energy.management.pattern.observer.AlertObserver;
import com.energy.management.pattern.observer.AlertSubject;
import com.energy.management.repository.EnergyDataRepository;
import com.energy.management.repository.MeterRepository;
import com.energy.management.service.AlertService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSimulatorService {
    
    private final MeterRepository meterRepository;
    private final EnergyDataRepository energyDataRepository;
    private final AlertService alertService;
    private final AlertSubject alertSubject;
    private final List<AlertObserver> alertObservers;
    private final NormalEnergyDataFactory normalDataFactory;
    private final AbnormalEnergyDataFactory abnormalDataFactory;
    
    @Value("${simulator.enabled:true}")
    private boolean simulatorEnabled;
    
    @Value("${simulator.anomaly-frequency:30}")
    private int anomalyFrequency;
    
    private final AtomicInteger dataCounter = new AtomicInteger(0);
    
    @PostConstruct
    public void init() {
        log.info("============================================");
        log.info("能耗数据模拟器初始化");
        log.info("模拟器状态: {}", simulatorEnabled ? "已启用" : "已禁用");
        log.info("异常数据频率: 每 {} 条正常数据生成1条异常数据", anomalyFrequency);
        log.info("============================================");
        
        alertObservers.forEach(alertSubject::registerObserver);
        log.info("已注册 {} 个告警观察者", alertSubject.getObserverCount());
    }
    
    @Scheduled(fixedRateString = "${simulator.interval:5000}")
    @Transactional
    public void generateEnergyData() {
        if (!simulatorEnabled) {
            return;
        }
        
        List<Meter> onlineDevices = meterRepository.findByStatus(DeviceStatus.ONLINE);
        
        if (onlineDevices.isEmpty()) {
            log.debug("没有在线设备，跳过数据生成");
            return;
        }
        
        log.debug("开始为 {} 个在线设备生成能耗数据", onlineDevices.size());
        
        for (Meter device : onlineDevices) {
            try {
                generateDataForDevice(device);
            } catch (Exception e) {
                log.error("设备[{}]数据生成失败: {}", device.getSerialNumber(), e.getMessage(), e);
            }
        }
        
        log.debug("本轮数据生成完成，总计数: {}", dataCounter.get());
    }
    
    private void generateDataForDevice(Meter device) {
        Double lastTotalEnergy = energyDataRepository
                .findLatestTotalEnergyByDeviceId(device.getId())
                .orElse(0.0);
        
        int count = dataCounter.incrementAndGet();
        
        EnergyData energyData;
        if (count % anomalyFrequency == 0) {
            log.info("故障注入：为设备[{}]生成异常数据", device.getSerialNumber());
            energyData = abnormalDataFactory.createEnergyData(device, lastTotalEnergy);
        } else {
            energyData = normalDataFactory.createEnergyData(device, lastTotalEnergy);
        }
        
        energyDataRepository.save(energyData);
        
        log.debug("设备[{}] - 电压: {}V, 电流: {}A, 功率: {}W, 累计: {}kWh, 异常: {}",
                device.getSerialNumber(),
                energyData.getVoltage(),
                energyData.getCurrent(),
                energyData.getPower(),
                energyData.getTotalEnergy(),
                energyData.getIsAbnormal());
        
        alertService.checkAndTriggerAlerts(device, energyData);
    }
    
    public void triggerManualGeneration() {
        log.info("手动触发能耗数据生成");
        generateEnergyData();
    }
    
    public void setSimulatorEnabled(boolean enabled) {
        this.simulatorEnabled = enabled;
        log.info("模拟器状态已更改为: {}", enabled ? "启用" : "禁用");
    }
    
    public boolean isSimulatorEnabled() {
        return simulatorEnabled;
    }
    
    public int getGeneratedDataCount() {
        return dataCounter.get();
    }
}
