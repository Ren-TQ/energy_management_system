package com.campus.energy.pattern.observer;

import com.campus.energy.entity.Alert;

/**
 * ============================================
 * 设计模式：Observer Pattern（观察者模式）
 * ============================================
 * 
 * 模式类型：行为型设计模式
 * 
 * 模式说明：
 * 定义对象间的一种一对多依赖关系，使得每当一个对象状态发生改变时，
 * 其相关依赖对象都得到通知并自动更新。
 * 
 * 在此项目中的应用：
 * - Observer接口：AlertObserver（观察者接口）
 * - Subject：AlertSubject（被观察者/主题）
 * - ConcreteObserver：DatabaseAlertObserver（数据库观察者）
 * - ConcreteObserver：LogAlertObserver（日志观察者）
 * 
 * 使用场景：
 * 当告警触发时，需要同时执行多个操作：
 * 1. 保存告警到数据库
 * 2. 记录告警日志
 * 3. 未来可扩展：发送邮件、短信通知等
 * 
 * 优势：
 * 1. 解耦主题和观察者，降低耦合度
 * 2. 支持动态添加/删除观察者
 * 3. 符合开闭原则，易于扩展新的观察者
 * 
 * 代码位置：
 * - 观察者接口：com.campus.energy.pattern.observer.AlertObserver
 * - 主题类：com.campus.energy.pattern.observer.AlertSubject
 * - 使用位置：AlertService.checkAndTriggerAlerts()
 * ============================================
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

