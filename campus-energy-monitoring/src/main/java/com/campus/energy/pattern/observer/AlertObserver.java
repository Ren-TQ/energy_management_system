package com.campus.energy.pattern.observer;

import com.campus.energy.entity.Alert;

/**
 * ============================================
 * 设计模式：Observer Pattern（观察者模式）- 观察者接口
 * ============================================
 * 
 * 模式类型：行为型设计模式
 * 
 * 模式说明：
 * 定义对象间的一种一对多依赖关系，使得每当一个对象状态发生改变时，
 * 其相关依赖对象都得到通知并自动更新。
 * 在此项目中的应用：
 * - Observer接口：AlertObserver（本接口）- 定义观察者的统一接口
 * - Subject：AlertSubject（被观察者/主题）- 管理观察者列表并通知
 * - ConcreteObserver：DatabaseAlertObserver（数据库观察者）- 保存告警到数据库
 * - ConcreteObserver：LogAlertObserver（日志观察者）- 记录告警日志
 * 当告警触发时，需要同时执行多个操作，且这些操作相互独立：
 * 1. 保存告警到数据库（DatabaseAlertObserver）
 * 2. 记录告警日志（LogAlertObserver）
 * 
 * 执行流程：
 * 1. 观察者注册：在系统初始化时，所有观察者注册到AlertSubject
 * 2. 告警触发：策略模式检测到异常，创建告警对象
 * 3. 通知观察者：AlertSubject调用notifyObservers()通知所有观察者
 * 4. 观察者响应：每个观察者执行自己的onAlertTriggered()方法
 * 5. 独立处理：每个观察者独立处理告警，互不影响
 * 
 * 优势：
 * 1. 解耦主题和观察者：主题不需要知道具体有哪些观察者
 * 2. 支持动态添加/删除观察者：运行时可以注册或移除观察者
 * 3. 符合开闭原则：新增观察者只需实现接口，无需修改主题代码
 * 4. 一对多通知：一个主题可以通知多个观察者
 * 5. 观察者独立：一个观察者失败不影响其他观察者
 * ============================================
 */
public interface AlertObserver {
    
    /**
     * 当告警触发时调用此方法
     * 
     * 观察者模式核心方法：当主题（AlertSubject）状态改变时，会调用所有观察者的此方法
     * 
     * 方法职责：
     * 1. 接收告警对象
     * 2. 执行观察者特定的处理逻辑
     * 3. 处理过程中如果发生异常，应该捕获并记录，不影响其他观察者
     *
     */
    void onAlertTriggered(Alert alert);
    
    /**
     * 获取观察者名称
     */
    String getObserverName();
}

