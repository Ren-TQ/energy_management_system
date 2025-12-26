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

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {
    
    private final AlertRepository alertRepository;
    private final AlertSubject alertSubject;
    private final List<AlertStrategy> alertStrategies;
    
    @PostConstruct
    public void init() {
        log.info("告警服务初始化，已加载 {} 个告警策略", alertStrategies.size());
        alertStrategies.forEach(strategy -> 
            log.info("  - {}", strategy.getStrategyName()));
    }
    
    @Override
    @Transactional
    public void checkAndTriggerAlerts(Meter device, EnergyData energyData) {
        for (AlertStrategy strategy : alertStrategies) {
            Optional<Alert> alertOpt = strategy.checkAlert(device, energyData);
            
            if (alertOpt.isPresent()) {
                Alert alert = alertOpt.get();
                log.info("策略[{}]检测到异常，触发告警", strategy.getStrategyName());
                
                alertSubject.notifyObservers(alert);
            }
        }
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
        
        alert.setIsResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolveNote(resolveNote);
        
        alert = alertRepository.save(alert);
        log.info("告警已处理，ID: {}", alertId);
        
        return convertToDTO(alert);
    }
    
    @Override
    public List<AlertDTO> getAlertsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return alertRepository.findByTimeRange(startTime, endTime).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
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
