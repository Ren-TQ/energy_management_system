package com.energy.management.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert")
@Data
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "meter_id", nullable = false)
    private Meter meter;

    @Enumerated(EnumType.STRING)
    private AlertType alertType;

    @Column(name = "alert_value")
    private Double alertValue;

    @Column(name = "alert_detail")
    private String alertDetail;

    @Column(name = "trigger_time")
    private LocalDateTime triggerTime;

    @Column(name = "is_resolved")
    private Boolean isResolved = false;

    @PrePersist
    protected void onCreate() {
        triggerTime = LocalDateTime.now();
    }

    public enum AlertType {
        POWER_OVERLOAD,      // 功率超限
        VOLTAGE_ABNORMAL,    // 电压异常
        DEVICE_OFFLINE       // 设备离线
    }
}