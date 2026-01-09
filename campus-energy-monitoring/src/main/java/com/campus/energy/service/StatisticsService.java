package com.campus.energy.service;

import com.campus.energy.dto.StatisticsDTO;
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
 * ============================================
 * 设计模式：Facade Pattern（外观模式）
 * ============================================
 * 
 * 模式类型：结构型设计模式
 * 在此项目中的应用：
 * - Facade（外观类）：StatisticsService（本类）
 *   - 为Controller层提供统一的统计数据接口
 *   - 隐藏了多个Repository的复杂交互
 *   - 封装了复杂的统计计算逻辑
 * 
 * - Subsystem（子系统）：
 *   - BuildingRepository：建筑数据访问
 *   - DeviceRepository：设备数据访问
 *   - AlertRepository：告警数据访问
 *   - EnergyDataRepository：能耗数据访问
 * 
 * - Client（客户端）：StatisticsController
 *   - 只需要调用getOverviewStatistics()一个方法
 *   - 不需要知道内部调用了哪些Repository
 *   - 不需要知道统计计算逻辑
 * 
 * 外观模式优势体现：
 * 1. 简化客户端调用：
 *    - Controller只需要调用statisticsService.getOverviewStatistics()
 *    - 不需要知道内部调用了4个Repository
 *    - 不需要知道复杂的统计计算逻辑
 * 
 * 2. 隐藏子系统复杂性：
 *    - 封装了多个Repository的调用
 *    - 封装了复杂的统计计算（如用电量计算）
 *    - 封装了数据聚合逻辑
 * 
 * 3. 降低耦合度：
 *    - Controller与多个Repository解耦
 *    - 子系统变化不影响Controller
 *
 * // Service层（外观类）
 * public StatisticsDTO getOverviewStatistics() {
 *     // 外观模式：封装了多个Repository的调用和复杂计算
 *     // 内部调用了4个Repository，进行了复杂的统计计算
 *     // 但客户端不需要知道这些细节
 * }
 * 
 * 代码位置：
 * - 外观类：com.campus.energy.service.StatisticsService（本类）
 * - 子系统：com.campus.energy.repository.BuildingRepository
 * - 子系统：com.campus.energy.repository.DeviceRepository
 * - 子系统：com.campus.energy.repository.AlertRepository
 * - 子系统：com.campus.energy.repository.EnergyDataRepository
 * - 客户端：com.campus.energy.controller.StatisticsController
 * ============================================
 * 
 * 统计数据服务层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {
    
    // ============================================
    // 设计模式：Facade Pattern（外观模式）- 子系统
    // ============================================
    // 
    // 外观模式：子系统组件
    // 
    // 说明：
    // 这些Repository是外观模式中的子系统组件
    // StatisticsService（外观类）封装了这些子系统的复杂交互
    // Controller（客户端）不需要直接访问这些Repository
    // 
    // 子系统职责：
    // - BuildingRepository：建筑数据访问
    // - DeviceRepository：设备数据访问
    // - AlertRepository：告警数据访问
    // - EnergyDataRepository：能耗数据访问
    // ============================================
    private final BuildingRepository buildingRepository;  // 子系统：建筑数据访问层
    private final DeviceRepository deviceRepository;  // 子系统：设备数据访问层
    private final AlertRepository alertRepository;  // 子系统：告警数据访问层
    private final EnergyDataRepository energyDataRepository;  // 子系统：能耗数据访问层
    
    /**
     * 获取系统概览统计数据
     * 
     * ============================================
     * 设计模式：Facade Pattern（外观模式）- 核心方法
     * ============================================
     * 
     * 外观模式：提供统一的统计数据接口
     * 
     * 执行流程：
     * 1. 调用BuildingRepository获取建筑统计
     * 2. 调用DeviceRepository获取设备统计
     * 3. 调用AlertRepository获取告警统计
     * 4. 调用EnergyDataRepository获取能耗统计
     * 5. 进行复杂的数据聚合和计算
     * 6. 封装所有统计数据到StatisticsDTO
     * 7. 返回DTO给客户端
     * 
     * 外观模式优势体现：
     * - 客户端只需要调用此方法，获取所有统计数据
     * - 不需要知道内部调用了4个Repository
     * - 不需要知道复杂的统计计算逻辑
     * - 不需要知道数据聚合过程
     * - 所有复杂的子系统交互都被封装在此方法中
     * ============================================
     */
    public StatisticsDTO getOverviewStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
        
        // ============================================
        // 外观模式：封装子系统调用
        // 调用BuildingRepository获取基础统计
        // ============================================
        // 基础统计
        long buildingCount = buildingRepository.count();
        
        // ============================================
        // 外观模式：封装子系统调用
        // 调用DeviceRepository获取设备统计
        // ============================================
        long deviceCount = deviceRepository.count();
        long onlineDeviceCount = deviceRepository.countByStatus(DeviceStatus.ONLINE);
        long offlineDeviceCount = deviceRepository.countByStatus(DeviceStatus.OFFLINE);
        
        // ============================================
        // 外观模式：封装子系统调用
        // 调用AlertRepository获取告警统计
        // ============================================
        // 告警统计
        long todayAlertCount = alertRepository.countTodayAlerts(startOfDay);
        long unresolvedAlertCount = alertRepository.countByIsResolvedFalse();
        
        // ============================================
        // 外观模式：封装复杂的计算逻辑
        // 调用EnergyDataRepository计算能耗统计
        // ============================================
        // 能耗统计（简化计算）
        Double todayTotalEnergy = calculateTotalEnergy(startOfDay, now);
        Double monthTotalEnergy = calculateTotalEnergy(startOfMonth, now);
        
        // ============================================
        // 外观模式：封装复杂的数据聚合
        // 各建筑用电量统计（涉及多个Repository的交互）
        // ============================================
        // 各建筑用电量统计
        List<StatisticsDTO.BuildingEnergyStats> buildingEnergyStats = getBuildingEnergyStats();
        
        // ============================================
        // 外观模式：封装子系统调用
        // 告警类型统计
        // ============================================
        // 告警类型统计
        var alertTypeStats = getAlertTypeStats();
        
        // ============================================
        // 外观模式：封装数据聚合
        // 将所有统计数据封装到DTO对象
        // ============================================
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
     * 
     * ============================================
     * 设计模式：Facade Pattern（外观模式）
     * ============================================
     * 
     * 外观模式：封装复杂的计算逻辑
     * 
     * 执行流程：
     * 1. 调用DeviceRepository获取所有设备
     * 2. 遍历每个设备，调用EnergyDataRepository计算用电量
     * 3. 累加所有设备的用电量
     * 
     * 外观模式优势体现：
     * - 隐藏了需要调用两个Repository的复杂性
     * - 隐藏了数据聚合和计算逻辑
     * - 客户端不需要知道计算过程
     * ============================================
     */
    private Double calculateTotalEnergy(LocalDateTime startTime, LocalDateTime endTime) {
        // ============================================
        // 外观模式：封装子系统调用
        // 调用DeviceRepository获取所有设备
        // ============================================
        // 获取所有设备的用电量并累加
        return deviceRepository.findAll().stream()
                .mapToDouble(device -> {
                    // ============================================
                    // 外观模式：封装子系统调用
                    // 调用EnergyDataRepository计算每个设备的用电量
                    // ============================================
                    Double consumption = energyDataRepository.calculateEnergyConsumption(
                            device.getId(), startTime, endTime);
                    return consumption != null ? consumption : 0.0;
                })
                .sum();  // 外观模式：封装数据聚合逻辑
    }
    
    /**
     * 获取各建筑用电量统计
     * 
     * ============================================
     * 设计模式：Facade Pattern（外观模式）
     * ============================================
     * 
     * 外观模式：封装复杂的数据聚合逻辑
     * 
     * 执行流程：
     * 1. 调用BuildingRepository获取所有建筑
     * 2. 对每个建筑：
     *    - 调用DeviceRepository统计设备数量
     *    - 调用DeviceRepository获取该建筑下的设备
     *    - 对每个设备调用EnergyDataRepository计算用电量
     *    - 累加该建筑下所有设备的用电量
     * 3. 封装为BuildingEnergyStats对象列表
     * 
     * 外观模式优势体现：
     * - 隐藏了需要调用3个Repository的复杂性
     * - 隐藏了复杂的数据聚合和计算逻辑
     * - 客户端不需要知道统计过程
     * ============================================
     */
    private List<StatisticsDTO.BuildingEnergyStats> getBuildingEnergyStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
        
        // ============================================
        // 外观模式：封装子系统调用
        // 调用BuildingRepository获取所有建筑
        // ============================================
        return buildingRepository.findAll().stream()
                .map(building -> {
                    // ============================================
                    // 外观模式：封装子系统调用
                    // 调用DeviceRepository统计设备数量
                    // ============================================
                    int deviceCount = (int) deviceRepository.countByBuildingId(building.getId());
                    
                    // ============================================
                    // 外观模式：封装复杂的数据聚合
                    // 调用DeviceRepository和EnergyDataRepository计算用电量
                    // ============================================
                    // 计算该建筑下所有设备的总用电量
                    double totalEnergy = deviceRepository.findByBuildingId(building.getId()).stream()
                            .mapToDouble(device -> {
                                // 外观模式：调用EnergyDataRepository计算每个设备的用电量
                                Double consumption = energyDataRepository.calculateEnergyConsumption(
                                        device.getId(), startOfMonth, now);
                                return consumption != null ? consumption : 0.0;
                            })
                            .sum();  // 外观模式：封装数据聚合逻辑
                    
                    // ============================================
                    // 外观模式：封装数据封装
                    // 将统计结果封装为DTO对象
                    // ============================================
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

