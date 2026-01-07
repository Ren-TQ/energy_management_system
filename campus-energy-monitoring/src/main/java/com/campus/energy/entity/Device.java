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
 * ============================================
 * MVC架构 - Model层（模型层）- 数据模型部分
 * ============================================
 * 
 * 架构说明：
 * 本类属于MVC架构中的Model（M）层，负责：
 * 1. 定义数据模型结构（对应数据库表）
 * 2. 使用JPA注解映射数据库表
 * 3. 封装业务数据
 * 
 * MVC职责划分：
 * - Controller (C): DeviceController - 接收请求
 * - Model (M): 本类（Entity实体）- 数据模型
 *              DeviceService - 业务逻辑
 *              DeviceRepository - 数据访问
 * - View (V): Result<DeviceDTO> - JSON响应
 * 
 * 数据流转：
 * 数据库表 ← JPA映射 → Device实体（本类）→ Service → Controller → DTO → JSON响应
 * 
 * 注意：
 * - Entity不直接暴露给Controller，通过DTO传输
 * - Entity用于Service层和Repository层之间的数据传递
 * ============================================
 * 
 * ============================================
 * 领域模型类型：贫血模型（Anemic Domain Model）
 * ============================================
 * 
 * 模型说明：
 * 本类采用贫血模型设计，特点如下：
 * 
 * 1. 只包含数据字段和getter/setter方法
 * 2. 不包含业务逻辑方法（业务逻辑在Service层）
 * 3. 使用Lombok的@Data自动生成getter/setter
 * 4. 使用JPA注解进行数据映射
 * 
 * 业务逻辑位置：
 * - 所有业务逻辑都在DeviceService层
 * - Entity只负责数据存储和映射
 * 
 * 优势：
 * - 结构简单，易于理解
 * - 符合Spring/JPA的常见实践
 * - 业务逻辑集中管理
 * - 易于序列化和DTO转换
 * 
 * 示例：
 * - 设备状态更新逻辑 → DeviceService.updateDeviceStatus()
 * - 设备验证逻辑 → DeviceService.createDevice()
 * - 设备查询逻辑 → DeviceService.getAllDevices()
 * 
 * 注意：
 * 虽然Martin Fowler将贫血模型视为反模式，但在Spring项目中这是常见且合理的实践。
 * ============================================
 * 
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

