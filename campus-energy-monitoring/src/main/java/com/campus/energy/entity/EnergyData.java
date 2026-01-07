package com.campus.energy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 能耗数据实体类
 * 用于存储智能电表采集的实时能耗数据
 */
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
    
    /**
     * 所属设备
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
    
    /**
     * 当前电压 (V)
     * 标准值: 220V, 正常范围: 210V-235V
     */
    @Column(name = "voltage", nullable = false)
    private Double voltage;
    
    /**
     * 当前电流 (A)
     * 根据公式 I = P / U 计算
     */
    @Column(name = "current", nullable = false)
    private Double current;
    
    /**
     * 当前实时功率 (W)
     * 满足公式 P = U × I
     */
    @Column(name = "power", nullable = false)
    private Double power;
    
    /**
     * 累计用电量 (kWh)
     */
    @Column(name = "total_energy", nullable = false)
    private Double totalEnergy;
    
    /**
     * 采集时间戳
     */
    @Column(name = "collect_time", nullable = false)
    private LocalDateTime collectTime;
    
    /**
     * 数据是否异常
     */
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

