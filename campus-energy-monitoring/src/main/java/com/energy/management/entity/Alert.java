package com.energy.management.entity;

import com.energy.management.enums.AlertType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Meter device;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 50)
    private AlertType alertType;
    
    @Column(name = "alert_value", nullable = false)
    private Double alertValue;
    
    @Column(name = "threshold_value")
    private Double thresholdValue;
    
    @Column(name = "description", nullable = false, length = 500)
    private String description;
    
    @Column(name = "is_resolved")
    @Builder.Default
    private Boolean isResolved = false;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @Column(name = "resolve_note", length = 500)
    private String resolveNote;
    
    @Column(name = "trigger_time", nullable = false)
    private LocalDateTime triggerTime;
    
    @PrePersist
    protected void onCreate() {
        if (this.triggerTime == null) {
            this.triggerTime = LocalDateTime.now();
        }
    }
}
