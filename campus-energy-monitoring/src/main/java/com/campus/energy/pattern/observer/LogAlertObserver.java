package com.campus.energy.pattern.observer;

import com.campus.energy.entity.Alert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ============================================
 * 设计模式：Observer Pattern（观察者模式）- 具体观察者
 * ============================================
 * 职责：当告警触发时，将告警信息记录到日志系统
 * 日志级别：
 * - 使用WARN级别，表示警告信息
 * - 便于在日志系统中筛选和监控告警
 * - 格式统一，便于日志分析工具处理
 * 
 * 记录内容：
 * - 告警类型、设备信息、告警数值、阈值、描述、触发时间等
 * - 格式化的输出，便于阅读和分析
 * 
 * 使用场景：
 * - 实时监控告警情况
 * - 日志分析和问题排查
 * - 告警审计和追溯
 * ============================================
 */
@Slf4j
@Component  // Spring自动管理，单例模式，自动注册到AlertSubject
public class LogAlertObserver implements AlertObserver {
    
    /**
     * 观察者模式核心方法：当告警触发时执行
     */
    @Override
    public void onAlertTriggered(Alert alert) {
        // ============================================
        // 观察者模式：执行观察者特定的处理逻辑
        // 将告警信息记录到日志系统
        // ============================================
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
    
    /**
     * 获取观察者名称
     */
    @Override
    public String getObserverName() {
        return "日志记录观察者";
    }
}

