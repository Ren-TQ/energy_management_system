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
 * 1. 管理观察者列表（注册、移除、查询）
 * 2. 当状态改变时通知所有观察者
 * 3. 保证通知过程的线程安全和异常隔离
 * 线程安全说明：
 * CopyOnWriteArrayList的特点：
 * - 读操作：无锁，性能高
 * - 写操作：复制数组，线程安全
 * - 适合读多写少的场景（观察者列表在初始化后很少修改）
 * ============================================
 */
@Slf4j
@Component  // Spring自动管理，单例模式，整个应用只有一个AlertSubject实例
public class AlertSubject {
    
    /**
     * 观察者列表
     * 
     * 线程安全说明：
     * - 使用CopyOnWriteArrayList保证线程安全
     * - 读操作（遍历通知）无锁，性能高
     * - 写操作（注册/移除）会复制数组，保证线程安全
     * - 适合读多写少的场景（观察者列表在初始化后很少修改）
     */
    private final List<AlertObserver> observers = new CopyOnWriteArrayList<>();
    
    /**
     * 注册观察者
     * 
     * 观察者模式核心方法：将观察者添加到观察者列表
     */
    public void registerObserver(AlertObserver observer) {
        // 检查是否已存在，避免重复注册
        if (!observers.contains(observer)) {
            observers.add(observer);
            log.info("注册告警观察者: {}", observer.getObserverName());
        } else {
            log.debug("观察者[{}]已存在，跳过注册", observer.getObserverName());
        }
    }
    
    /**
     * 移除观察者
     */
    public void removeObserver(AlertObserver observer) {
        boolean removed = observers.remove(observer);
        if (removed) {
            log.info("移除告警观察者: {}", observer.getObserverName());
        } else {
            log.debug("观察者[{}]不存在，无法移除", observer.getObserverName());
        }
    }
    
    /**
     * 通知所有观察者
     *
     */
    public void notifyObservers(Alert alert) {
        // 记录通知日志
        log.info("通知所有观察者，告警类型: {}, 设备: {}", 
                alert.getAlertType(), 
                alert.getDevice().getName());
        
        // ============================================
        // 观察者模式核心：遍历所有观察者并通知
        // ============================================
        for (AlertObserver observer : observers) {
            try {
                // 观察者模式：多态调用，每个观察者执行自己的逻辑
                observer.onAlertTriggered(alert);
            } catch (Exception e) {
                // 异常隔离：一个观察者失败不影响其他观察者
                log.error("观察者[{}]处理告警时发生异常: {}", 
                        observer.getObserverName(), e.getMessage(), e);
            }
        }
        
        log.debug("已通知 {} 个观察者", observers.size());
    }
    
    /**
     * 获取观察者数量
     * 
     * 用于监控和调试，查看当前注册了多少个观察者
     *
     */
    public int getObserverCount() {
        return observers.size();
    }
}

