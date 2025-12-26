package com.energy.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_energy_data", indexes = {
    @Index(name = "idx_device_id", columnList = "device_id"),
    @Index(name = "idx_collect_time", columnList = "collect_time")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnergyData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Meter device;
    
    @Column(name = "voltage", nullable = false)
    private Double voltage;
    
    @Column(name = "current", nullable = false)
    private Double current;
    
    @Column(name = "power", nullable = false)
    private Double power;
    
    @Column(name = "total_energy", nullable = false)
    private Double totalEnergy;
    
    @Column(name = "collect_time", nullable = false)
    private LocalDateTime collectTime;
    
    @Column(name = "is_abnormal")
    @Builder.Default
    private Boolean isAbnormal = false;
    
    @PrePersist
    protected void onCreate() {
        if (this.collectTime == null) {
            this.collectTime = LocalDateTime.now();
        }
    }
}
