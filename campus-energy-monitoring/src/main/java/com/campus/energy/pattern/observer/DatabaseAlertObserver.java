package com.campus.energy.pattern.observer;

import com.campus.energy.entity.Alert;
import com.campus.energy.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Pattern: Observer - 数据库存储观察者
 * 
 * 具体观察者：负责将告警信息持久化到数据库
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseAlertObserver implements AlertObserver {
    
    private final AlertRepository alertRepository;
    
    @Override
    public void onAlertTriggered(Alert alert) {
        log.info("数据库观察者：保存告警到数据库，设备: {}, 类型: {}", 
                alert.getDevice().getName(), 
                alert.getAlertType());
        
        alertRepository.save(alert);
        
        log.debug("告警已保存到数据库，ID: {}", alert.getId());
    }
    
    @Override
    public String getObserverName() {
        return "数据库存储观察者";
    }
}

