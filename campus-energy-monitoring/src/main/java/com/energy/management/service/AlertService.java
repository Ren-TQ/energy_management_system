package com.energy.management.service;

import com.energy.management.dto.AlertDTO;
import com.energy.management.entity.EnergyData;
import com.energy.management.entity.Meter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AlertService {
    
    void checkAndTriggerAlerts(Meter device, EnergyData energyData);
    
    Page<AlertDTO> getAllAlerts(Pageable pageable);
    
    Page<AlertDTO> getAlertsByDeviceId(Long deviceId, Pageable pageable);
    
    List<AlertDTO> getUnresolvedAlerts();
    
    List<AlertDTO> getRecentAlerts();
    
    long getTodayAlertCount();
    
    long getUnresolvedAlertCount();
    
    Map<String, Long> getAlertTypeStats();
    
    AlertDTO resolveAlert(Long alertId, String resolveNote);
    
    List<AlertDTO> getAlertsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
}
