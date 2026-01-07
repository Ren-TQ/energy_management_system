package com.campus.energy.pattern.observer;

import com.campus.energy.entity.Alert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ============================================
 * 设计模式：Observer Pattern（观察者模式）- 主题类
 * ============================================
 * 
 * 角色：Subject（被观察者/主题）
 * 
 * 职责：
 * 1. 管理观察者列表（注册、移除）
 * 2. 当状态改变时通知所有观察者
 * 
 * 实现细节：
 * - 使用CopyOnWriteArrayList保证线程安全
 * - 支持动态注册和移除观察者
 * - 通知时捕获异常，避免一个观察者失败影响其他观察者
 * ============================================
 */
@Slf4j
@Component
public class AlertSubject {
    
    /**
     * 观察者列表，使用线程安全的集合
     */
    private final List<AlertObserver> observers = new CopyOnWriteArrayList<>();
    
    /**
     * 注册观察者
     * 
     * @param observer 要注册的观察者
     */
    public void registerObserver(AlertObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            log.info("注册告警观察者: {}", observer.getObserverName());
        }
    }
    
    /**
     * 移除观察者
     * 
     * @param observer 要移除的观察者
     */
    public void removeObserver(AlertObserver observer) {
        observers.remove(observer);
        log.info("移除告警观察者: {}", observer.getObserverName());
    }
    
    /**
     * 通知所有观察者
     * 
     * @param alert 告警对象
     */
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
    
    /**
     * 获取观察者数量
     * 
     * @return 观察者数量
     */
    public int getObserverCount() {
        return observers.size();
    }
}

