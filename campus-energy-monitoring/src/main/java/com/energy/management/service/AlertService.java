package com.energy.management.service;

import com.energy.management.dto.AlertDTO;
import com.energy.management.entity.EnergyData;
import com.energy.management.entity.Meter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 告警服务层
 * 
 * Pattern: Strategy - 使用策略模式进行告警判断
 * Pattern: Observer - 使用观察者模式进行告警通知
 */
public interface AlertService {
    
    /**
     * 检查能耗数据是否触发告警
     * Pattern: Strategy - 遍历所有策略进行告警判断
     * Pattern: Observer - 触发告警时通知所有观察者
     */
    void checkAndTriggerAlerts(Meter device, EnergyData energyData);
    
    /**
     * 获取所有告警记录（分页）
     */
    Page<AlertDTO> getAllAlerts(Pageable pageable);
    
    /**
     * 根据设备ID获取告警记录（分页）
     */
    Page<AlertDTO> getAlertsByDeviceId(Long deviceId, Pageable pageable);
    
    /**
     * 获取未处理的告警
     */
    List<AlertDTO> getUnresolvedAlerts();
    
    /**
     * 获取最近的告警记录
     */
    List<AlertDTO> getRecentAlerts();
    
    /**
     * 获取今日告警数量
     */
    long getTodayAlertCount();
    
    /**
     * 获取未处理告警数量
     */
    long getUnresolvedAlertCount();
    
    /**
     * 统计各类型告警数量
     */
    Map<String, Long> getAlertTypeStats();
    
    /**
     * 处理告警
     */
    AlertDTO resolveAlert(Long alertId, String resolveNote);
    
    /**
     * 批量处理告警
     */
    int batchResolveAlerts(List<Long> alertIds, String resolveNote);
    
    /**
     * 根据时间范围获取告警
     */
    List<AlertDTO> getAlertsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
}
