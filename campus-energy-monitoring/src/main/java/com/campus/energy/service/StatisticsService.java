package com.campus.energy.service;

import com.campus.energy.dto.StatisticsDTO;
import com.campus.energy.entity.Building;
import com.campus.energy.enums.DeviceStatus;
import com.campus.energy.repository.AlertRepository;
import com.campus.energy.repository.BuildingRepository;
import com.campus.energy.repository.DeviceRepository;
import com.campus.energy.repository.EnergyDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统计数据服务层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {
    
    private final BuildingRepository buildingRepository;
    private final DeviceRepository deviceRepository;
    private final AlertRepository alertRepository;
    private final EnergyDataRepository energyDataRepository;
    
    /**
     * 获取系统概览统计数据
     */
    public StatisticsDTO getOverviewStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
        
        // 基础统计
        long buildingCount = buildingRepository.count();
        long deviceCount = deviceRepository.count();
        long onlineDeviceCount = deviceRepository.countByStatus(DeviceStatus.ONLINE);
        long offlineDeviceCount = deviceRepository.countByStatus(DeviceStatus.OFFLINE);
        
        // 告警统计
        long todayAlertCount = alertRepository.countTodayAlerts(startOfDay);
        long unresolvedAlertCount = alertRepository.countByIsResolvedFalse();
        
        // 能耗统计（简化计算）
        Double todayTotalEnergy = calculateTotalEnergy(startOfDay, now);
        Double monthTotalEnergy = calculateTotalEnergy(startOfMonth, now);
        
        // 各建筑用电量统计
        List<StatisticsDTO.BuildingEnergyStats> buildingEnergyStats = getBuildingEnergyStats();
        
        // 告警类型统计
        var alertTypeStats = getAlertTypeStats();
        
        return StatisticsDTO.builder()
                .buildingCount(buildingCount)
                .deviceCount(deviceCount)
                .onlineDeviceCount(onlineDeviceCount)
                .offlineDeviceCount(offlineDeviceCount)
                .todayAlertCount(todayAlertCount)
                .unresolvedAlertCount(unresolvedAlertCount)
                .todayTotalEnergy(todayTotalEnergy)
                .monthTotalEnergy(monthTotalEnergy)
                .buildingEnergyStats(buildingEnergyStats)
                .alertTypeStats(alertTypeStats)
                .build();
    }
    
    /**
     * 计算时间范围内的总用电量
     */
    private Double calculateTotalEnergy(LocalDateTime startTime, LocalDateTime endTime) {
        // 获取所有设备的用电量并累加
        return deviceRepository.findAll().stream()
                .mapToDouble(device -> {
                    Double consumption = energyDataRepository.calculateEnergyConsumption(
                            device.getId(), startTime, endTime);
                    return consumption != null ? consumption : 0.0;
                })
                .sum();
    }
    
    /**
     * 获取各建筑用电量统计
     */
    private List<StatisticsDTO.BuildingEnergyStats> getBuildingEnergyStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
        
        return buildingRepository.findAll().stream()
                .map(building -> {
                    int deviceCount = (int) deviceRepository.countByBuildingId(building.getId());
                    
                    // 计算该建筑下所有设备的总用电量
                    double totalEnergy = deviceRepository.findByBuildingId(building.getId()).stream()
                            .mapToDouble(device -> {
                                Double consumption = energyDataRepository.calculateEnergyConsumption(
                                        device.getId(), startOfMonth, now);
                                return consumption != null ? consumption : 0.0;
                            })
                            .sum();
                    
                    return StatisticsDTO.BuildingEnergyStats.builder()
                            .buildingId(building.getId())
                            .buildingName(building.getName())
                            .deviceCount(deviceCount)
                            .totalEnergy(Math.round(totalEnergy * 100.0) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 获取告警类型统计
     */
    private java.util.Map<String, Long> getAlertTypeStats() {
        List<Object[]> stats = alertRepository.countByAlertType();
        java.util.Map<String, Long> result = new java.util.HashMap<>();
        
        for (Object[] row : stats) {
            com.campus.energy.enums.AlertType type = (com.campus.energy.enums.AlertType) row[0];
            Long count = (Long) row[1];
            result.put(type.getLabel(), count);
        }
        
        return result;
    }
}

