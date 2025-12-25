package com.energy.management.service.observer;

import com.energy.management.entity.Alert;
import com.energy.management.repository.AlertRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// Pattern: Observer - 事件监听器
@Component
@Slf4j
public class AlertListener {

    @Autowired
    private AlertRepository alertRepository;

    @EventListener
    @Async
    @Transactional
    public void handleAlertEvent(AlertEvent event) {
        log.info("处理告警事件: {} - {}", event.getAlertType(), event.getDetail());

        // 创建告警记录
        Alert alert = new Alert();
        alert.setMeter(event.getMeter());
        alert.setAlertType(event.getAlertType());
        alert.setAlertValue(event.getValue());
        alert.setAlertDetail(event.getDetail());
        alert.setIsResolved(false);

        // 保存到数据库
        alertRepository.save(alert);

        log.info("告警记录已保存，ID: {}", alert.getId());

        // 这里可以添加其他处理逻辑，比如：
        // 1. 发送WebSocket通知到前端
        // 2. 记录到日志文件
        // 3. 触发其他业务逻辑
    }
}