package com.campus.energy.entity;

import com.campus.energy.enums.DeviceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 智能电表设备实体类
 * 用于存储智能电表设备的基本信息
 */
@Entity
@Table(name = "t_device", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"serial_number"}),
    @UniqueConstraint(columnNames = {"building_id", "room_number"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 设备名称
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /**
     * 唯一设备序列号 (SN)
     * 格式如：METER_2025_001 或 M_BLD01_R101
     */
    @Column(name = "serial_number", nullable = false, unique = true, length = 50)
    private String serialNumber;
    
    /**
     * 通讯状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private DeviceStatus status = DeviceStatus.ONLINE;
    
    /**
     * 额定功率阈值 (W)
     */
    @Column(name = "rated_power", nullable = false)
    private Double ratedPower;
    
    /**
     * 所属建筑
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;
    
    /**
     * 房间号
     */
    @Column(name = "room_number", nullable = false, length = 50)
    private String roomNumber;
    
    /**
     * 设备用途描述
     */
    @Column(name = "usage_description", length = 200)
    private String usageDescription;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 该设备的所有能耗数据
     */
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EnergyData> energyDataList = new ArrayList<>();
    
    /**
     * 该设备的所有告警记录
     */
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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

