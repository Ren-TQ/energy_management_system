package com.energy.management.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "meter")
@Data
public class Meter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @Column(name = "serial_number", nullable = false, unique = true)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status = DeviceStatus.ONLINE;

    @Column(name = "power_threshold")
    private Double powerThreshold;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private Boolean active = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum DeviceStatus {
        ONLINE, OFFLINE
    }
    
    public boolean isActive() {
        return active != null ? active : false;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public Boolean getActive() {
        return active;
    }
}