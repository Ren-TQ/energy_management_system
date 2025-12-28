package com.energy.management.service.impl;

import com.energy.management.dto.AlertDTO;
import com.energy.management.entity.Alert;
import com.energy.management.entity.EnergyData;
import com.energy.management.entity.Meter;
import com.energy.management.enums.AlertType;
import com.energy.management.exception.BusinessException;
import com.energy.management.pattern.observer.AlertSubject;
import com.energy.management.pattern.strategy.AlertStrategy;
import com.energy.management.repository.AlertRepository;
import com.energy.management.service.AlertService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 告警服务实现
 * 
 * 优化点：
 * 1. 告警去重：避免短时间内重复告警
 * 2. 性能优化：减少数据库查询
 * 3. 批量处理：支持批量处理告警
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {
    
    private final AlertRepository alertRepository;
    private final AlertSubject alertSubject;
    private final List<AlertStrategy> alertStrategies;
    
    /**
     * 告警去重时间窗口（分钟）
     * 同一设备同一类型告警在此时间窗口内只触发一次
     */
    @Value("${alert.deduplication.window-minutes:5}")
    private int deduplicationWindowMinutes;
    
    @PostConstruct
    public void init() {
        log.info("告警服务初始化，已加载 {} 个告警策略", alertStrategies.size());
        alertStrategies.forEach(strategy -> 
            log.info("  - {}", strategy.getStrategyName()));
        log.info("告警去重时间窗口: {} 分钟", deduplicationWindowMinutes);
    }
    
    /**
     * 检查能耗数据是否触发告警
     * 优化：添加告警去重逻辑，避免短时间内重复告警
     */
    @Override
    @Transactional
    public void checkAndTriggerAlerts(Meter device, EnergyData energyData) {
        for (AlertStrategy strategy : alertStrategies) {
            Optional<Alert> alertOpt = strategy.checkAlert(device, energyData);
            
            if (alertOpt.isPresent()) {
                Alert alert = alertOpt.get();
                
                // 检查是否需要去重
                if (shouldDeduplicateAlert(device.getId(), alert.getAlertType())) {
                    log.debug("告警已去重，设备: {}, 类型: {}", 
                            device.getName(), alert.getAlertType().getLabel());
                    continue;
                }
                
                log.info("策略[{}]检测到异常，触发告警", strategy.getStrategyName());
                
                // 通知所有观察者（观察者模式）
                // 注意：DatabaseAlertObserver会负责保存告警
                alertSubject.notifyObservers(alert);
            }
        }
    }
    
    /**
     * 判断是否需要去重告警
     * 如果同一设备同一类型在时间窗口内有未处理的告警，则去重
     */
    private boolean shouldDeduplicateAlert(Long deviceId, AlertType alertType) {
        Alert latestAlert = alertRepository.findLatestByDeviceIdAndAlertType(deviceId, alertType);
        
        if (latestAlert == null || latestAlert.getIsResolved()) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(latestAlert.getTriggerTime(), now);
        
        // 如果最新告警在时间窗口内且未处理，则去重
        if (duration.toMinutes() < deduplicationWindowMinutes) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public Page<AlertDTO> getAllAlerts(Pageable pageable) {
        return alertRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    public Page<AlertDTO> getAlertsByDeviceId(Long deviceId, Pageable pageable) {
        return alertRepository.findByDeviceId(deviceId, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    public List<AlertDTO> getUnresolvedAlerts() {
        return alertRepository.findByIsResolvedFalse().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AlertDTO> getRecentAlerts() {
        return alertRepository.findTop10ByOrderByTriggerTimeDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public long getTodayAlertCount() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        return alertRepository.countTodayAlerts(startOfDay);
    }
    
    @Override
    public long getUnresolvedAlertCount() {
        return alertRepository.countByIsResolvedFalse();
    }
    
    @Override
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
    
    @Override
    @Transactional
    public AlertDTO resolveAlert(Long alertId, String resolveNote) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new BusinessException("告警不存在，ID: " + alertId));
        
        if (alert.getIsResolved()) {
            throw new BusinessException("告警已处理，ID: " + alertId);
        }
        
        alert.setIsResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolveNote(resolveNote);
        
        alert = alertRepository.save(alert);
        log.info("告警已处理，ID: {}", alertId);
        
        return convertToDTO(alert);
    }
    
    /**
     * 批量处理告警
     * 优化：支持批量处理，提高效率
     */
    @Override
    @Transactional
    public int batchResolveAlerts(List<Long> alertIds, String resolveNote) {
        if (alertIds == null || alertIds.isEmpty()) {
            return 0;
        }
        
        List<Alert> alerts = alertRepository.findAllById(alertIds);
        
        if (alerts.size() != alertIds.size()) {
            throw new BusinessException("部分告警ID不存在");
        }
        
        LocalDateTime now = LocalDateTime.now();
        int resolvedCount = 0;
        
        for (Alert alert : alerts) {
            if (!alert.getIsResolved()) {
                alert.setIsResolved(true);
                alert.setResolvedAt(now);
                alert.setResolveNote(resolveNote);
                resolvedCount++;
            }
        }
        
        alertRepository.saveAll(alerts);
        log.info("批量处理告警完成，共处理 {} 条", resolvedCount);
        
        return resolvedCount;
    }
    
    @Override
    public List<AlertDTO> getAlertsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return alertRepository.findByTimeRange(startTime, endTime).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 转换为DTO
     */
    private AlertDTO convertToDTO(Alert alert) {
        Meter device = alert.getDevice();
        
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
