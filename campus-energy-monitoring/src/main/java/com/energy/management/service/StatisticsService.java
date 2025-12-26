package com.energy.management.service;

import com.energy.management.dto.StatisticsDTO;
import com.energy.management.enums.DeviceStatus;
import com.energy.management.repository.AlertRepository;
import com.energy.management.repository.BuildingRepository;
import com.energy.management.repository.EnergyDataRepository;
import com.energy.management.repository.MeterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {
    
    private final BuildingRepository buildingRepository;
    private final MeterRepository meterRepository;
    private final AlertRepository alertRepository;
    private final EnergyDataRepository energyDataRepository;
    
    public StatisticsDTO getOverviewStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
        
        long buildingCount = buildingRepository.count();
        long deviceCount = meterRepository.count();
        long onlineDeviceCount = meterRepository.countByStatus(DeviceStatus.ONLINE);
        long offlineDeviceCount = meterRepository.countByStatus(DeviceStatus.OFFLINE);
        
        long todayAlertCount = alertRepository.countTodayAlerts(startOfDay);
        long unresolvedAlertCount = alertRepository.countByIsResolvedFalse();
        
        Double todayTotalEnergy = calculateTotalEnergy(startOfDay, now);
        Double monthTotalEnergy = calculateTotalEnergy(startOfMonth, now);
        
        List<StatisticsDTO.BuildingEnergyStats> buildingEnergyStats = getBuildingEnergyStats();
        
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
    
    private Double calculateTotalEnergy(LocalDateTime startTime, LocalDateTime endTime) {
        return meterRepository.findAll().stream()
                .mapToDouble(device -> {
                    Double consumption = energyDataRepository.calculateEnergyConsumption(
                            device.getId(), startTime, endTime);
                    return consumption != null ? consumption : 0.0;
                })
                .sum();
    }
    
    private List<StatisticsDTO.BuildingEnergyStats> getBuildingEnergyStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
        
        return buildingRepository.findAll().stream()
                .map(building -> {
                    int deviceCount = (int) meterRepository.countByBuildingId(building.getId());
                    
                    double totalEnergy = meterRepository.findByBuildingId(building.getId()).stream()
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
    
    private Map<String, Long> getAlertTypeStats() {
        List<Object[]> stats = alertRepository.countByAlertType();
        Map<String, Long> result = new HashMap<>();
        
        for (Object[] row : stats) {
            com.energy.management.enums.AlertType type = (com.energy.management.enums.AlertType) row[0];
            Long count = (Long) row[1];
            result.put(type.getLabel(), count);
        }
        
        return result;
    }
}

