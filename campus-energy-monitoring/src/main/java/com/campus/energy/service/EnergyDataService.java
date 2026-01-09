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
 * 
 * ============================================
 * 设计模式：Facade Pattern（外观模式）
 * ============================================
 * 
 * 模式类型：结构型设计模式
 * 
 * 模式说明：
 * 外观模式为子系统中的一组接口提供一个统一的高层接口。
 * 它定义了一个更简单的接口，隐藏了子系统的复杂性。
 * 
 * 在此项目中的应用：
 * - Facade（外观类）：EnergyDataService（本类）
 *   - 为Controller层提供简化的能耗数据操作接口
 *   - 隐藏了Repository层的复杂交互
 *   - 封装了业务逻辑和数据转换
 * 
 * - Subsystem（子系统）：
 *   - EnergyDataRepository：能耗数据访问层
 *   - Entity/DTO转换逻辑
 *   - 业务逻辑（如分页、时间范围查询、用电量计算）
 * 
 * - Client（客户端）：EnergyDataController
 *   - 只需要调用Service的简单方法
 *   - 不需要了解Repository的复杂交互
 * 
 * 外观模式优势体现：
 * 1. 简化客户端调用：
 *    - Controller只需要调用energyDataService.getEnergyDataByDeviceId()
 *    - 不需要知道内部调用了哪些Repository、如何分页、如何转换数据
 * 
 * 2. 隐藏子系统复杂性：
 *    - 封装了Repository的调用
 *    - 封装了Entity和DTO之间的转换逻辑
 *    - 封装了分页逻辑、时间范围查询逻辑
 * 
 * 3. 降低耦合度：
 *    - Controller与Repository解耦
 *    - 子系统变化不影响Controller
 * 
 * 代码位置：
 * - 外观类：com.campus.energy.service.EnergyDataService（本类）
 * - 子系统：com.campus.energy.repository.EnergyDataRepository
 * - 客户端：com.campus.energy.controller.EnergyDataController
 * ============================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EnergyDataService {
    
    // ============================================
    // 设计模式：Facade Pattern（外观模式）- 子系统
    // ============================================
    // EnergyDataRepository是外观模式中的子系统组件
    // EnergyDataService（外观类）封装了Repository的调用
    // Controller（客户端）不需要直接访问Repository
    // ============================================
    private final EnergyDataRepository energyDataRepository;  // 子系统：能耗数据访问层
    
    /**
     * 保存能耗数据
     */
    @Transactional
    public EnergyData saveEnergyData(EnergyData energyData) {
        return energyDataRepository.save(energyData);
    }

    /**
     * 根据设备ID获取能耗数据（分页）
     *
     * ============================================
     * 设计模式：Facade Pattern（外观模式）
     * ============================================
     *
     * 外观模式：提供简化的接口给客户端
     *
     * 执行流程：
     * 1. 调用子系统（EnergyDataRepository）获取分页数据
     * 2. 封装Entity到DTO的转换逻辑
     * 3. 返回分页DTO给客户端
     *
     * 外观模式优势体现：
     * - 客户端只需要调用此方法，传入设备ID和分页参数
     * - 不需要知道内部调用了哪个Repository
     * - 不需要知道Entity如何转换为DTO
     * - 不需要知道分页如何实现
     * ============================================
     */
    public Page<EnergyDataDTO> getEnergyDataByDeviceId(Long deviceId, Pageable pageable) {
        // ============================================
        // 外观模式：封装子系统调用
        // 调用EnergyDataRepository获取分页数据
        // 外观模式：封装Entity到DTO的转换
        // ============================================
        return energyDataRepository.findByDeviceId(deviceId, pageable)
                .map(this::convertToDTO);  // 外观模式：封装转换逻辑
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

