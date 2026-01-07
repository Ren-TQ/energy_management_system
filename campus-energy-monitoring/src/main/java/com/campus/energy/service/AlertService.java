package com.campus.energy.service;

import com.campus.energy.dto.AlertDTO;
import com.campus.energy.entity.Alert;
import com.campus.energy.entity.Device;
import com.campus.energy.entity.EnergyData;
import com.campus.energy.enums.AlertType;
import com.campus.energy.exception.BusinessException;
import com.campus.energy.pattern.observer.AlertSubject;
import com.campus.energy.pattern.strategy.AlertStrategy;
import com.campus.energy.repository.AlertRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 告警服务层
 * 
 * Pattern: Strategy - 使用策略模式进行告警判断
 * Pattern: Observer - 使用观察者模式进行告警通知
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {
    
    private final AlertRepository alertRepository;
    private final AlertSubject alertSubject;
    private final List<AlertStrategy> alertStrategies;
    
    @PostConstruct
    public void init() {
        log.info("告警服务初始化，已加载 {} 个告警策略", alertStrategies.size());
        alertStrategies.forEach(strategy -> 
            log.info("  - {}", strategy.getStrategyName()));
    }
    
    /**
     * 检查能耗数据是否触发告警
     * Pattern: Strategy - 遍历所有策略进行告警判断
     * Pattern: Observer - 触发告警时通知所有观察者
     */
    @Transactional
    public void checkAndTriggerAlerts(Device device, EnergyData energyData) {
        for (AlertStrategy strategy : alertStrategies) {
            Optional<Alert> alertOpt = strategy.checkAlert(device, energyData);
            
            if (alertOpt.isPresent()) {
                Alert alert = alertOpt.get();
                log.info("策略[{}]检测到异常，触发告警", strategy.getStrategyName());
                
                // 通知所有观察者（观察者模式）
                // 注意：DatabaseAlertObserver会负责保存告警
                alertSubject.notifyObservers(alert);
            }
        }
    }
    
    /**
     * 获取所有告警记录（分页）
     */
    public Page<AlertDTO> getAllAlerts(Pageable pageable) {
        return alertRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
    
    /**
     * 根据设备ID获取告警记录（分页）
     */
    public Page<AlertDTO> getAlertsByDeviceId(Long deviceId, Pageable pageable) {
        return alertRepository.findByDeviceId(deviceId, pageable)
                .map(this::convertToDTO);
    }
    
    /**
     * 获取未处理的告警
     */
    public List<AlertDTO> getUnresolvedAlerts() {
        return alertRepository.findByIsResolvedFalse().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取最近的告警记录
     */
    public List<AlertDTO> getRecentAlerts() {
        return alertRepository.findTop10ByOrderByTriggerTimeDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取今日告警数量
     */
    public long getTodayAlertCount() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        return alertRepository.countTodayAlerts(startOfDay);
    }
    
    /**
     * 获取未处理告警数量
     */
    public long getUnresolvedAlertCount() {
        return alertRepository.countByIsResolvedFalse();
    }
    
    /**
     * 统计各类型告警数量
     */
    public Map<String, Long> getAlertTypeStats() {
        List<Object[]> stats = alertRepository.countByAlertType();
        Map<String, Long> result = new HashMap<>();
        
        for (Object[] row : stats) {
            AlertType type = (AlertType) row[0];
            Long count = (Long) row[1];
            result.put(type.getLabel(), count);
        }
        
        return result;
    }
    
    /**
     * 处理告警
     */
    @Transactional
    public AlertDTO resolveAlert(Long alertId, String resolveNote) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new BusinessException("告警不存在，ID: " + alertId));
        
        alert.setIsResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolveNote(resolveNote);
        
        alert = alertRepository.save(alert);
        log.info("告警已处理，ID: {}", alertId);
        
        return convertToDTO(alert);
    }
    
    /**
     * 根据时间范围获取告警
     */
    public List<AlertDTO> getAlertsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return alertRepository.findByTimeRange(startTime, endTime).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 转换为DTO
     */
    private AlertDTO convertToDTO(Alert alert) {
        Device device = alert.getDevice();
        
        return AlertDTO.builder()
                .id(alert.getId())
                .deviceId(device.getId())
                .deviceName(device.getName())
                .deviceSerialNumber(device.getSerialNumber())
                .buildingName(device.getBuilding().getName())
                .roomNumber(device.getRoomNumber())
                .alertType(alert.getAlertType())
                .alertTypeLabel(alert.getAlertType().getLabel())
                .alertValue(alert.getAlertValue())
                .thresholdValue(alert.getThresholdValue())
                .description(alert.getDescription())
                .isResolved(alert.getIsResolved())
                .resolvedAt(alert.getResolvedAt())
                .resolveNote(alert.getResolveNote())
                .triggerTime(alert.getTriggerTime())
                .build();
    }
}

