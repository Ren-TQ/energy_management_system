package com.campus.energy.simulator;

import com.campus.energy.entity.Device;
import com.campus.energy.entity.EnergyData;
import com.campus.energy.enums.DeviceStatus;
import com.campus.energy.pattern.factory.AbnormalEnergyDataFactory;
import com.campus.energy.pattern.factory.NormalEnergyDataFactory;
import com.campus.energy.pattern.observer.AlertObserver;
import com.campus.energy.pattern.observer.AlertSubject;
import com.campus.energy.repository.DeviceRepository;
import com.campus.energy.repository.EnergyDataRepository;
import com.campus.energy.service.AlertService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 能耗数据模拟器服务
 * 
 * 定时任务：每隔固定时间生成所有在线设备的能耗数据
 * 
 * Pattern: Factory - 使用工厂模式创建正常/异常能耗数据
 * Pattern: Observer - 通过观察者模式触发告警通知
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EnergySimulatorService {
    
    private final DeviceRepository deviceRepository;
    private final EnergyDataRepository energyDataRepository;
    private final AlertService alertService;
    private final AlertSubject alertSubject;
    private final List<AlertObserver> alertObservers;
    
    // Pattern: Factory - 正常数据工厂
    private final NormalEnergyDataFactory normalDataFactory;
    
    // Pattern: Factory - 异常数据工厂
    private final AbnormalEnergyDataFactory abnormalDataFactory;
    
    @Value("${simulator.enabled:true}")
    private boolean simulatorEnabled;
    
    @Value("${simulator.anomaly-frequency:30}")
    private int anomalyFrequency;
    
    /**
     * 数据计数器，用于控制异常数据的生成频率
     */
    private final AtomicInteger dataCounter = new AtomicInteger(0);
    
    @PostConstruct
    public void init() {
        log.info("============================================");
        log.info("能耗数据模拟器初始化");
        log.info("模拟器状态: {}", simulatorEnabled ? "已启用" : "已禁用");
        log.info("异常数据频率: 每 {} 条正常数据生成1条异常数据", anomalyFrequency);
        log.info("============================================");
        
        // Pattern: Observer - 注册所有告警观察者
        alertObservers.forEach(alertSubject::registerObserver);
        log.info("已注册 {} 个告警观察者", alertSubject.getObserverCount());
    }
    
    /**
     * 定时任务：生成能耗数据
     * 每5秒执行一次（可在配置文件中修改）
     */
    @Scheduled(fixedRateString = "${simulator.interval:5000}")
    @Transactional
    public void generateEnergyData() {
        if (!simulatorEnabled) {
            return;
        }
        
        // 获取所有在线设备
        List<Device> onlineDevices = deviceRepository.findByStatus(DeviceStatus.ONLINE);
        
        if (onlineDevices.isEmpty()) {
            log.debug("没有在线设备，跳过数据生成");
            return;
        }
        
        log.debug("开始为 {} 个在线设备生成能耗数据", onlineDevices.size());
        
        for (Device device : onlineDevices) {
            try {
                generateDataForDevice(device);
            } catch (Exception e) {
                log.error("设备[{}]数据生成失败: {}", device.getSerialNumber(), e.getMessage(), e);
            }
        }
        
        log.debug("本轮数据生成完成，总计数: {}", dataCounter.get());
    }
    
    /**
     * 为单个设备生成能耗数据
     */
    private void generateDataForDevice(Device device) {
        // 获取设备最新的累计用电量
        Double lastTotalEnergy = energyDataRepository
                .findLatestTotalEnergyByDeviceId(device.getId())
                .orElse(0.0);
        
        // 增加计数器
        int count = dataCounter.incrementAndGet();
        
        // Pattern: Factory - 根据计数决定使用哪个工厂
        // 每 anomalyFrequency 条数据生成一条异常数据（故障注入）
        EnergyData energyData;
        if (count % anomalyFrequency == 0) {
            log.info("故障注入：为设备[{}]生成异常数据", device.getSerialNumber());
            energyData = abnormalDataFactory.createEnergyData(device, lastTotalEnergy);
        } else {
            energyData = normalDataFactory.createEnergyData(device, lastTotalEnergy);
        }
        
        // 保存能耗数据
        energyDataRepository.save(energyData);
        
        log.debug("设备[{}] - 电压: {}V, 电流: {}A, 功率: {}W, 累计: {}kWh, 异常: {}",
                device.getSerialNumber(),
                energyData.getVoltage(),
                energyData.getCurrent(),
                energyData.getPower(),
                energyData.getTotalEnergy(),
                energyData.getIsAbnormal());
        
        // 检查是否触发告警（使用策略模式进行判断）
        alertService.checkAndTriggerAlerts(device, energyData);
    }
    
    /**
     * 手动触发数据生成（用于测试）
     */
    public void triggerManualGeneration() {
        log.info("手动触发能耗数据生成");
        generateEnergyData();
    }
    
    /**
     * 启用/禁用模拟器
     */
    public void setSimulatorEnabled(boolean enabled) {
        this.simulatorEnabled = enabled;
        log.info("模拟器状态已更改为: {}", enabled ? "启用" : "禁用");
    }
    
    /**
     * 获取模拟器状态
     */
    public boolean isSimulatorEnabled() {
        return simulatorEnabled;
    }
    
    /**
     * 获取已生成的数据总数
     */
    public int getGeneratedDataCount() {
        return dataCounter.get();
    }
}

