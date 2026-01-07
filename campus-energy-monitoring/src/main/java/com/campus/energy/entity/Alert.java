package com.campus.energy.entity;

import com.campus.energy.enums.AlertType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ============================================
 * 领域模型类型：贫血模型（Anemic Domain Model）
 * ============================================
 * 
 * 模型说明：
 * 本类采用贫血模型设计，只包含数据字段，不包含业务逻辑方法。
 * 所有业务逻辑（如告警检查、告警处理）都在AlertService层处理。
 * ============================================
 * 
 * 异常告警记录实体类
 * 用于存储设备异常告警信息
 */
@Entity
@Table(name = "t_alert", indexes = {
    @Index(name = "idx_alert_device_id", columnList = "device_id"),
    @Index(name = "idx_trigger_time", columnList = "trigger_time")
})
@Data
// ============================================
// 设计模式：Builder Pattern（建造者模式）
// ============================================
// 在此项目中的应用：
// - 使用Lombok的@Builder注解自动生成建造者
// - 通过Alert.builder()创建Alert对象
// - 主要用于策略模式中创建告警对象
// 
// 使用场景：
// 1. 策略模式创建告警：PowerOverloadAlertStrategy、VoltageAbnormalAlertStrategy
// 2. 测试代码：创建测试数据时使用
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 所属设备
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
    
    /**
     * 告警类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 50)
    private AlertType alertType;
    
    /**
     * 告警数值
     */
    @Column(name = "alert_value", nullable = false)
    private Double alertValue;
    
    /**
     * 阈值 (用于对比)
     */
    @Column(name = "threshold_value")
    private Double thresholdValue;
    
    /**
     * 告警详情
     */
    @Column(name = "description", nullable = false, length = 500)
    private String description;
    
    /**
     * 是否已处理
     */
    @Column(name = "is_resolved")
    @Builder.Default
    private Boolean isResolved = false;
    
    /**
     * 处理时间
     */
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    /**
     * 处理备注
     */
    @Column(name = "resolve_note", length = 500)
    private String resolveNote;
    
    /**
     * 触发时间
     */
    @Column(name = "trigger_time", nullable = false)
    private LocalDateTime triggerTime;
    
    @PrePersist
    protected void onCreate() {
        if (this.triggerTime == null) {
            this.triggerTime = LocalDateTime.now();
        }
    }
}

