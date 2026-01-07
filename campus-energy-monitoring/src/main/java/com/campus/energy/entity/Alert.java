package com.campus.energy.entity;

import com.campus.energy.enums.AlertType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 异常告警记录实体类
 * 用于存储设备异常告警信息
 */
@Entity
@Table(name = "t_alert", indexes = {
    @Index(name = "idx_alert_device_id", columnList = "device_id"),
    @Index(name = "idx_trigger_time", columnList = "trigger_time")
})
@Data
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

