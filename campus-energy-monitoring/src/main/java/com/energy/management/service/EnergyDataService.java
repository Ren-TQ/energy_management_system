package com.energy.management.service;

import com.energy.management.dto.EnergyDataDTO;
import com.energy.management.entity.EnergyData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EnergyDataService {
    
    EnergyData saveEnergyData(EnergyData energyData);
    
    Page<EnergyDataDTO> getEnergyDataByDeviceId(Long deviceId, Pageable pageable);
    
    Optional<EnergyDataDTO> getLatestEnergyData(Long deviceId);
    
    List<EnergyDataDTO> getEnergyDataByTimeRange(Long deviceId, 
                                                 LocalDateTime startTime, 
                                                 LocalDateTime endTime);
    
    List<EnergyDataDTO> getLatestEnergyDataForAllDevices();
    
    Double calculateEnergyConsumption(Long deviceId, 
                                     LocalDateTime startTime, 
                                     LocalDateTime endTime);
    
    Optional<Double> getLatestTotalEnergy(Long deviceId);
    
    List<EnergyDataDTO> getTodayEnergyData(Long deviceId);
}
