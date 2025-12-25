package com.energy.management.entity;

import com.energy.management.enums.DeviceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_device", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"serial_number"}),
    @UniqueConstraint(columnNames = {"building_id", "room_number"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "serial_number", nullable = false, unique = true, length = 50)
    private String serialNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private DeviceStatus status = DeviceStatus.ONLINE;
    
    @Column(name = "rated_power", nullable = false)
    private Double ratedPower;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;
    
    @Column(name = "room_number", nullable = false, length = 50)
    private String roomNumber;
    
    @Column(name = "usage_description", length = 200)
    private String usageDescription;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "meter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EnergyData> energyDataList = new ArrayList<>();
    
    @OneToMany(mappedBy = "meter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Alert> alerts = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
