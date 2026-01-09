package com.campus.energy.pattern.factory;

import com.campus.energy.entity.Device;
import com.campus.energy.entity.EnergyData;

import java.time.LocalDateTime;

/**
 * ============================================
 * 设计模式：Factory Pattern（工厂模式）
 * ============================================

 * 模式说明：
 * 定义一个创建对象的接口，让子类决定实例化哪一个类。
 * 工厂模式使一个类的实例化延迟到其子类。
 * 
 * 在此项目中的应用：
 * - 接口：EnergyDataFactory（工厂接口）
 * - 具体工厂：NormalEnergyDataFactory（正常数据工厂）
 * - 具体工厂：AbnormalEnergyDataFactory（异常数据工厂）
 * 
 * 使用场景：
 * 根据不同的需求（正常数据/异常数据）创建不同类型的EnergyData对象
 * 
 * 优势：
 * 1. 解耦对象的创建和使用
 * 2. 符合开闭原则，易于扩展新的工厂类型
 * 3. 统一对象创建接口
 * ============================================
 */
public interface EnergyDataFactory {
    
    /**
     * 创建能耗数据
     */
    EnergyData createEnergyData(Device device, Double lastTotalEnergy);
    
    /**
     * 获取工厂名称
     */
    String getFactoryName();
}

