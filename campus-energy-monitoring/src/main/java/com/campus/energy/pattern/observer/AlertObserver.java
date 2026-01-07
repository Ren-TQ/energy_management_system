package com.campus.energy.pattern.observer;

import com.campus.energy.entity.Alert;

/**
 * Pattern: Observer - 告警观察者接口
 * 
 * 观察者模式：定义对象间的一种一对多依赖关系，使得每当一个对象状态发生改变时，
 * 其相关依赖对象都得到通知并自动更新。
 * 
 * 此接口用于定义告警事件的观察者，当告警触发时，所有观察者都会收到通知。
 */
public interface AlertObserver {
    
    /**
     * 当告警触发时调用此方法
     * 
     * @param alert 告警对象
     */
    void onAlertTriggered(Alert alert);
    
    /**
     * 获取观察者名称
     * 
     * @return 观察者名称
     */
    String getObserverName();
}

