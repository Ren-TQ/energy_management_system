package com.campus.energy.pattern.observer;

import com.campus.energy.entity.Alert;
import com.campus.energy.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ============================================
 * 设计模式：Observer Pattern（观察者模式）- 具体观察者
 * ============================================
 * 
 * 角色：ConcreteObserver（具体观察者）
 * 
 * 职责：当告警触发时，将告警信息持久化到数据库
 *
 * 使用场景：
 * - 告警记录需要持久化，便于后续查询和分析
 * - 告警历史记录，用于问题追溯
 * - 告警统计和分析
 * ============================================
 */
@Slf4j
@Component  // Spring自动管理，单例模式，自动注册到AlertSubject
@RequiredArgsConstructor
public class DatabaseAlertObserver implements AlertObserver {
    
    /**
     * 告警数据访问层
     * 
     * 用于将告警对象保存到数据库
     */
    private final AlertRepository alertRepository;
    
    /**
     * 观察者模式核心方法：当告警触发时执行
     * 
     * 执行流程：
     * 1. 接收告警对象（来自AlertSubject的通知）
     * 2. 记录日志（便于调试和监控）
     * 3. 保存告警到数据库
     * 4. 记录保存结果（可选）
     * 异常处理：
     * - 如果保存失败，会抛出异常
     * - AlertSubject会捕获异常并记录日志，不影响其他观察者
     */
    @Override
    public void onAlertTriggered(Alert alert) {
        // 记录处理日志
        log.info("数据库观察者：保存告警到数据库，设备: {}, 类型: {}", 
                alert.getDevice().getName(), 
                alert.getAlertType());
        
        // ============================================
        // 观察者模式：执行观察者特定的处理逻辑
        // 将告警对象持久化到数据库
        // ============================================
        alertRepository.save(alert);
        
        // 记录保存结果（如果alert.getId()不为null，说明保存成功）
        log.debug("告警已保存到数据库，ID: {}", alert.getId());
    }
    
    /**
     * 获取观察者名称
     */
    @Override
    public String getObserverName() {
        return "数据库存储观察者";
    }
}

