package com.campus.energy.pattern.observer;

import com.campus.energy.entity.Alert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Pattern: Observer - 日志记录观察者
 * 
 * 具体观察者：负责将告警信息记录到日志系统
 */
@Slf4j
@Component
public class LogAlertObserver implements AlertObserver {
    
    @Override
    public void onAlertTriggered(Alert alert) {
        log.warn("============ 告警触发 ============");
        log.warn("告警类型: {}", alert.getAlertType().getLabel());
        log.warn("设备名称: {}", alert.getDevice().getName());
        log.warn("设备序列号: {}", alert.getDevice().getSerialNumber());
        log.warn("告警数值: {}", alert.getAlertValue());
        log.warn("阈值: {}", alert.getThresholdValue());
        log.warn("告警详情: {}", alert.getDescription());
        log.warn("触发时间: {}", alert.getTriggerTime());
        log.warn("=================================");
    }
    
    @Override
    public String getObserverName() {
        return "日志记录观察者";
    }
}

