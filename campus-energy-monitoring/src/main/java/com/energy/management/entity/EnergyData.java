package com.energy.management.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "energy_data", indexes = {
        @Index(name = "idx_meter_timestamp", columnList = "meter_id, timestamp")
})
@Data
public class EnergyData {
    
    @Column(name = "alert_detail")
    private String alertDetail;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "meter_id", nullable = false)
    private Meter meter;

    @Column(name = "voltage")
    private Double voltage;  // 电压(V)

    @Column(name = "current")
    private Double current;  // 电流(A)

    @Column(name = "power")
    private Double power;    // 功率(W)

    @Column(name = "energy_consumption")
    private Double energyConsumption;  // 累计用电量(kWh)

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}