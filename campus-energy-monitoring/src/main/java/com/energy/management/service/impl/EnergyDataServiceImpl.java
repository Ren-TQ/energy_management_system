package com.energy.management.service.impl;

import com.energy.management.dto.EnergyDataDTO;
import com.energy.management.entity.EnergyData;
import com.energy.management.entity.Meter;
import com.energy.management.repository.EnergyDataRepository;
import com.energy.management.service.EnergyDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnergyDataServiceImpl implements EnergyDataService {
    
    private final EnergyDataRepository energyDataRepository;
    
    @Override
    @Transactional
    public EnergyData saveEnergyData(EnergyData energyData) {
        return energyDataRepository.save(energyData);
    }
    
    @Override
    public Page<EnergyDataDTO> getEnergyDataByDeviceId(Long deviceId, Pageable pageable) {
        return energyDataRepository.findByDeviceId(deviceId, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    public Optional<EnergyDataDTO> getLatestEnergyData(Long deviceId) {
        return energyDataRepository.findTopByDeviceIdOrderByCollectTimeDesc(deviceId)
                .map(this::convertToDTO);
    }
    
    @Override
    public List<EnergyDataDTO> getEnergyDataByTimeRange(Long deviceId, 
                                                         LocalDateTime startTime, 
                                                         LocalDateTime endTime) {
        return energyDataRepository.findByDeviceIdAndTimeRange(deviceId, startTime, endTime)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<EnergyDataDTO> getLatestEnergyDataForAllDevices() {
        return energyDataRepository.findLatestEnergyDataForAllDevices()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Double calculateEnergyConsumption(Long deviceId, 
                                             LocalDateTime startTime, 
                                             LocalDateTime endTime) {
        return energyDataRepository.calculateEnergyConsumption(deviceId, startTime, endTime);
    }
    
    @Override
    public Optional<Double> getLatestTotalEnergy(Long deviceId) {
        return energyDataRepository.findLatestTotalEnergyByDeviceId(deviceId);
    }
    
    @Override
    public List<EnergyDataDTO> getTodayEnergyData(Long deviceId) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        return getEnergyDataByTimeRange(deviceId, startOfDay, now);
    }
    
    private EnergyDataDTO convertToDTO(EnergyData energyData) {
        Meter device = energyData.getDevice();
        
        return EnergyDataDTO.builder()
                .id(energyData.getId())
                .deviceId(device.getId())
                .deviceName(device.getName())
                .deviceSerialNumber(device.getSerialNumber())
                .voltage(energyData.getVoltage())
                .current(energyData.getCurrent())
                .power(energyData.getPower())
                .totalEnergy(energyData.getTotalEnergy())
                .isAbnormal(energyData.getIsAbnormal())
                .collectTime(energyData.getCollectTime())
                .build();
    }
}
