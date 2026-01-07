package com.campus.energy.pattern.strategy;

import com.campus.energy.entity.Device;
import com.campus.energy.entity.EnergyData;
import com.campus.energy.entity.Alert;

import java.util.Optional;

/**
 * Pattern: Strategy - 告警判断策略接口
 * 
 * 策略模式：定义一系列算法，把它们一个个封装起来，并且使它们可以相互替换。
 * 此接口用于定义不同类型的告警判断策略，实现类可以根据不同的判断逻辑来检测异常。
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

