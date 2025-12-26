package com.energy.management.pattern.observer;

import com.energy.management.entity.Alert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class AlertSubject {
    
    private final List<AlertObserver> observers = new CopyOnWriteArrayList<>();
    
    public void registerObserver(AlertObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            log.info("注册告警观察者: {}", observer.getObserverName());
        }
    }
    
    public void removeObserver(AlertObserver observer) {
        observers.remove(observer);
        log.info("移除告警观察者: {}", observer.getObserverName());
    }
    
    public void notifyObservers(Alert alert) {
        log.info("通知所有观察者，告警类型: {}, 设备: {}", 
                alert.getAlertType(), 
                alert.getDevice().getName());
        
        for (AlertObserver observer : observers) {
            try {
                observer.onAlertTriggered(alert);
            } catch (Exception e) {
                log.error("观察者[{}]处理告警时发生异常: {}", 
                        observer.getObserverName(), e.getMessage(), e);
            }
        }
    }
    
    public int getObserverCount() {
        return observers.size();
    }
}

