package com.campus.energy.pattern.strategy;

import com.campus.energy.entity.Device;
import com.campus.energy.entity.EnergyData;
import com.campus.energy.entity.Alert;

import java.util.Optional;

/**
 * ============================================
 * 设计模式：Strategy Pattern（策略模式）
 * ============================================
 * 
 * 模式类型：行为型设计模式
 * 
 * 模式说明：
 * 定义一系列算法，把它们一个个封装起来，并且使它们可以相互替换。
 * 策略模式让算法的变化独立于使用算法的客户端。
 * 
 * 在此项目中的应用：
 * - Strategy接口：AlertStrategy（策略接口）
 * - ConcreteStrategy：PowerOverloadAlertStrategy（功率过载策略）
 * - ConcreteStrategy：VoltageAbnormalAlertStrategy（电压异常策略）
 * - Context：AlertService（使用策略的上下文）
 * 
 * 使用场景：
 * 不同类型的告警有不同的判断逻辑：
 * 1. 功率过载：检查功率是否超过额定功率的120%
 * 2. 电压异常：检查电压是否在正常范围内（198V-242V）
 * 3. 未来可扩展：电流异常、设备离线等策略
 * 
 * 优势：
 * 1. 算法可以自由切换
 * 2. 避免使用多重条件判断（if-else）
 * 3. 符合开闭原则，易于扩展新策略
 * 4. 每个策略类职责单一，符合单一职责原则
 * 
 * 代码位置：
 * - 策略接口：com.campus.energy.pattern.strategy.AlertStrategy
 * - 使用位置：AlertService.checkAndTriggerAlerts()
 * ============================================
 */
public interface AlertStrategy {
    
    /**
     * 判断是否需要触发告警
     * 
     * @param device 设备信息
     * @param energyData 能耗数据
     * @return 如果需要告警，返回告警对象；否则返回空
     */
    Optional<Alert> checkAlert(Device device, EnergyData energyData);
    
    /**
     * 获取策略名称
     * 
     * @return 策略名称
     */
    String getStrategyName();
}

