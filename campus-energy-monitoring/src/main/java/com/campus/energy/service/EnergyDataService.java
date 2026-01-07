package com.campus.energy.service;

import com.campus.energy.dto.EnergyDataDTO;
import com.campus.energy.entity.Device;
import com.campus.energy.entity.EnergyData;
import com.campus.energy.repository.EnergyDataRepository;
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

/**
 * ============================================
 * MVC架构 - Model层（模型层）- 业务逻辑部分
 * ============================================
 * 
 * 架构说明：
 * 本类属于MVC架构中的Model（M）层，负责处理能耗数据相关的业务逻辑
 * 
 * MVC职责划分：
 * - Controller (C): EnergyDataController - 接收请求
 * - Model (M): 本类（Service层）- 处理业务逻辑
 *              EnergyDataRepository - 数据访问
 *              EnergyData实体 - 数据模型
 * - View (V): Result<EnergyDataDTO> - JSON响应
 * ============================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EnergyDataService {
    
    // ============================================
    // MVC架构 - Model层（模型层）- 数据访问部分
    // Repository层：负责数据持久化，属于Model的一部分
    // ============================================
    private final EnergyDataRepository energyDataRepository;
    
    /**
     * 保存能耗数据
     */
    @Transactional
    public EnergyData saveEnergyData(EnergyData energyData) {
        return energyDataRepository.save(energyData);
    }
    
    /**
     * 根据设备ID获取能耗数据（分页）
     */
    public Page<EnergyDataDTO> getEnergyDataByDeviceId(Long deviceId, Pageable pageable) {
        return energyDataRepository.findByDeviceId(deviceId, pageable)
                .map(this::convertToDTO);
    }
    
    /**
     * 获取设备最新的能耗数据
     */
    public Optional<EnergyDataDTO> getLatestEnergyData(Long deviceId) {
        return energyDataRepository.findTopByDeviceIdOrderByCollectTimeDesc(deviceId)
                .map(this::convertToDTO);
    }
    
    /**
     * 获取设备在指定时间范围内的能耗数据
     */
    public List<EnergyDataDTO> getEnergyDataByTimeRange(Long deviceId, 
                                                         LocalDateTime startTime, 
                                                         LocalDateTime endTime) {
        return energyDataRepository.findByDeviceIdAndTimeRange(deviceId, startTime, endTime)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取所有设备的最新能耗数据
     */
    public List<EnergyDataDTO> getLatestEnergyDataForAllDevices() {
        return energyDataRepository.findLatestEnergyDataForAllDevices()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 计算设备在指定时间范围内的用电量
     */
    public Double calculateEnergyConsumption(Long deviceId, 
                                             LocalDateTime startTime, 
                                             LocalDateTime endTime) {
        return energyDataRepository.calculateEnergyConsumption(deviceId, startTime, endTime);
    }
    
    /**
     * 获取设备最新的累计用电量
     */
    public Optional<Double> getLatestTotalEnergy(Long deviceId) {
        return energyDataRepository.findLatestTotalEnergyByDeviceId(deviceId);
    }
    
    /**
     * 获取今日能耗数据
     */
    public List<EnergyDataDTO> getTodayEnergyData(Long deviceId) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        return getEnergyDataByTimeRange(deviceId, startOfDay, now);
    }
    
    /**
     * 转换为DTO
     */
    private EnergyDataDTO convertToDTO(EnergyData energyData) {
        Device device = energyData.getDevice();
        
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

