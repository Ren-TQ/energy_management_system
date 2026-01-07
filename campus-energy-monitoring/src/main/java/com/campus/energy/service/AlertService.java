package com.campus.energy.service;

import com.campus.energy.dto.AlertDTO;
import com.campus.energy.entity.Alert;
import com.campus.energy.entity.Device;
import com.campus.energy.entity.EnergyData;
import com.campus.energy.enums.AlertType;
import com.campus.energy.exception.BusinessException;
import com.campus.energy.pattern.observer.AlertSubject;
import com.campus.energy.pattern.strategy.AlertStrategy;
import com.campus.energy.repository.AlertRepository;
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

/**
 * 告警服务层
 * 
 * ============================================
 * 设计模式应用：
 * 1. Strategy Pattern（策略模式）- 使用策略模式进行告警判断
 * 2. Observer Pattern（观察者模式）- 使用观察者模式进行告警通知
 * ============================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {
    
    private final AlertRepository alertRepository;
    
    // ============================================
    // 设计模式：Observer Pattern（观察者模式）
    // 角色：Subject（主题），用于通知所有观察者
    // ============================================
    private final AlertSubject alertSubject;
    
    // ============================================
    // 设计模式：Strategy Pattern（策略模式）
    // ============================================
    // 
    // 角色：Context（上下文）
    // 
    // 说明：
    // Spring通过依赖注入，自动将所有实现了AlertStrategy接口的Bean注入到此列表中
    // 当前项目中的策略实现：
    // 1. PowerOverloadAlertStrategy（功率过载策略）
    // 2. VoltageAbnormalAlertStrategy（电压异常策略）
    // 
    // 策略模式优势体现：
    // - 运行时动态获取所有策略，无需硬编码
    // - 新增策略只需实现接口并添加@Component注解，自动生效
    // - 符合开闭原则：对扩展开放，对修改关闭
    // 
    // 注入机制：
    // Spring会自动扫描所有@Component注解且实现AlertStrategy接口的类
    // 创建单例Bean并注入到此List中
    // ============================================
    private final List<AlertStrategy> alertStrategies;
    
    /**
     * 初始化方法：在Bean创建后执行
     * 
     * 策略模式体现：
     * - 打印所有已加载的策略，便于调试和监控
     * - 验证策略是否正确注入
     */
    @PostConstruct
    public void init() {
        log.info("告警服务初始化，已加载 {} 个告警策略", alertStrategies.size());
        // 遍历所有策略，打印策略名称
        alertStrategies.forEach(strategy -> 
            log.info("  - {}", strategy.getStrategyName()));
    }
    
    /**
     * 检查能耗数据是否触发告警
     * 
     * ============================================
     * 设计模式：Strategy Pattern（策略模式）- 核心使用场景
     * ============================================
     * 
     * 方法职责：
     * 1. 遍历所有告警策略
     * 2. 让每个策略独立判断是否需要告警
     * 3. 如果策略检测到异常，触发告警通知（观察者模式）
     * @param device 设备信息，传递给策略进行判断
     * @param energyData 能耗数据，传递给策略进行判断
     * 
     * 调用位置：
     * - EnergySimulatorService.generateDataForDevice() - 生成数据后检查告警
     * ============================================
     */
    @Transactional
    public void checkAndTriggerAlerts(Device device, EnergyData energyData) {
        // ============================================
        // 策略模式核心：遍历所有策略
        // ============================================
        // 
        // 执行逻辑：
        // 1. 遍历alertStrategies列表中的所有策略实现
        // 2. 每个策略独立执行checkAlert()方法
        // 3. 策略返回Optional<Alert>，如果包含告警对象，则触发通知
        // ============================================
        for (AlertStrategy strategy : alertStrategies) {
            // 策略模式：多态调用，每个策略执行自己的判断逻辑
            Optional<Alert> alertOpt = strategy.checkAlert(device, energyData);
            if (alertOpt.isPresent()) {
                Alert alert = alertOpt.get();
                
                // 记录日志：哪个策略检测到了异常
                log.info("策略[{}]检测到异常，触发告警", strategy.getStrategyName());

                // 设计模式：Observer Pattern（观察者模式）
                // 
                // 观察者模式：通知所有观察者处理告警
                alertSubject.notifyObservers(alert);
            }
            // 如果策略返回Optional.empty()，表示未检测到异常

        }
    }
    
    /**
     * 获取所有告警记录（分页）
     */
    public Page<AlertDTO> getAllAlerts(Pageable pageable) {
        return alertRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
    
    /**
     * 根据设备ID获取告警记录（分页）
     */
    public Page<AlertDTO> getAlertsByDeviceId(Long deviceId, Pageable pageable) {
        return alertRepository.findByDeviceId(deviceId, pageable)
                .map(this::convertToDTO);
    }
    
    /**
     * 获取未处理的告警
     */
    public List<AlertDTO> getUnresolvedAlerts() {
        return alertRepository.findByIsResolvedFalse().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取最近的告警记录
     */
    public List<AlertDTO> getRecentAlerts() {
        return alertRepository.findTop10ByOrderByTriggerTimeDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取今日告警数量
     */
    public long getTodayAlertCount() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        return alertRepository.countTodayAlerts(startOfDay);
    }
    
    /**
     * 获取未处理告警数量
     */
    public long getUnresolvedAlertCount() {
        return alertRepository.countByIsResolvedFalse();
    }
    
    /**
     * 统计各类型告警数量
     */
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
    
    /**
     * 处理告警
     */
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
    
    /**
     * 根据时间范围获取告警
     */
    public List<AlertDTO> getAlertsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return alertRepository.findByTimeRange(startTime, endTime).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 转换为DTO
     */
    private AlertDTO convertToDTO(Alert alert) {
        Device device = alert.getDevice();
        
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

