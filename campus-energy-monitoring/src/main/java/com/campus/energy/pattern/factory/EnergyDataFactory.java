package com.campus.energy.pattern.factory;

import com.campus.energy.entity.Device;
import com.campus.energy.entity.EnergyData;

import java.time.LocalDateTime;

/**
 * Pattern: Factory - 能耗数据工厂接口
 * 
 * 工厂模式：定义一个创建对象的接口，让子类决定实例化哪一个类。
 * 此接口用于创建能耗数据对象，不同的工厂实现可以生成不同特征的能耗数据。
 */
public interface EnergyDataFactory {
    
    /**
     * 创建能耗数据
     * 
     * @param device 设备
     * @param lastTotalEnergy 上次累计用电量
     * @return 能耗数据对象
     */
    EnergyData createEnergyData(Device device, Double lastTotalEnergy);
    
    /**
     * 获取工厂名称
     * 
     * @return 工厂名称
     */
    String getFactoryName();
}

