package com.campus.energy.pattern.strategy;

import com.campus.energy.entity.Device;
import com.campus.energy.entity.EnergyData;
import com.campus.energy.entity.Alert;

import java.util.Optional;

/**
 * ============================================
 * 设计模式：Strategy Pattern（策略模式）- 策略接口
 * ============================================

 * 在此项目中的应用：
 * - Strategy接口：AlertStrategy（本接口）- 定义策略的统一接口
 * - ConcreteStrategy：PowerOverloadAlertStrategy（功率过载策略）- 具体策略实现1
 * - ConcreteStrategy：VoltageAbnormalAlertStrategy（电压异常策略）- 具体策略实现2
 * - Context：AlertService（使用策略的上下文）- 持有策略列表并执行
 * 
 * 使用场景：
 * 不同类型的告警有不同的判断逻辑，需要独立封装：
 * 1. 功率过载策略：检查功率是否超过额定功率的120%
 * 2. 电压异常策略：检查电压是否在正常范围内（198V-242V）
 * 
 * 执行流程：
 * 1. AlertService持有所有策略实现的列表（Spring自动注入）
 * 2. 当需要检查告警时，遍历所有策略
 * 3. 每个策略独立判断是否需要告警
 * 4. 如果策略返回告警对象，则触发告警通知（观察者模式）
 * 
 * 优势：
 * 1. 算法可以自由切换：运行时动态选择策略
 * 2. 避免使用多重条件判断：不需要if-else或switch-case
 * 3. 符合开闭原则：对扩展开放（新增策略），对修改关闭（不修改现有代码）
 * 4. 每个策略类职责单一：一个策略只负责一种告警类型的判断
 * 5. 易于测试：每个策略可以独立测试
 * 6. 易于维护：策略逻辑集中，修改不影响其他策略

 * ============================================
 */
public interface AlertStrategy {
    
    /**
     * 判断是否需要触发告警
     * 方法职责：
     * 1. 根据设备和能耗数据，判断是否符合告警条件
     * 2. 如果符合条件，构建并返回告警对象
     * 3. 如果不符合条件，返回Optional.empty()
     *
     */
    Optional<Alert> checkAlert(Device device, EnergyData energyData);
    
    /**
     * 获取策略名称
     * 
     * 用于日志记录和调试，标识当前使用的策略
     * 
     * @return 策略的中文名称，如"功率过载告警策略"、"电压异常告警策略"
     */
    String getStrategyName();
}

