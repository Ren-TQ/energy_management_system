package com.campus.energy.entity;

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
 * 本类属于MVC架构中的Model（M）层，负责定义建筑数据模型
 * 
 * MVC职责划分：
 * - Controller (C): BuildingController - 接收请求
 * - Model (M): 本类（Entity实体）- 数据模型
 *              BuildingService - 业务逻辑
 *              BuildingRepository - 数据访问
 * - View (V): Result<BuildingDTO> - JSON响应
 * ============================================
 * 
 * ============================================
 * 领域模型类型：贫血模型（Anemic Domain Model）
 * ============================================
 * 
 * 模型说明：
 * 本类采用贫血模型设计，只包含数据字段，不包含业务逻辑方法。
 * 所有业务逻辑都在BuildingService层处理。
 * ============================================
 * 
 * 建筑区域信息实体类
 * 用于存储校园内各建筑物的基本信息
 */
@Entity
@Table(name = "t_building")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Building {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 建筑名称
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    /**
     * 位置编号
     */
    @Column(name = "location_code", nullable = false, length = 50)
    private String locationCode;
    
    /**
     * 楼层数
     */
    @Column(name = "floor_count", nullable = false)
    private Integer floorCount;
    
    /**
     * 建筑用途分类
     * 如：教学楼、宿舍楼、办公楼、图书馆等
     */
    @Column(name = "category", nullable = false, length = 50)
    private String category;
    
    /**
     * 建筑描述
     */
    @Column(name = "description", length = 500)
    private String description;
    
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
     * 该建筑下的所有智能电表设备
     */
    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Device> devices = new ArrayList<>();
    
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

